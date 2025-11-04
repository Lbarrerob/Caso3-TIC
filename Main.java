import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        String configPath = args.length > 0 ? args[0] : "paramConfigurables.txt";

        Integer nClientes = null;
        Integer mensajesPorCliente = null;
        Integer nFiltros = null;
        Integer nServidores = null;
        Integer capacidadBuzonEntrada = null;
        Integer capacidadBuzonEntrega = null;

        File configFile = new File(configPath);
        if (!configFile.exists()) {
            System.err.println("[Main] ERROR: Archivo de configuración no encontrado en: " + configPath);
            System.exit(1);
        }

        try (BufferedReader br = new BufferedReader(new FileReader(configFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String lower = line.toLowerCase();

                try {
                    if (lower.contains("número de clientes") || lower.contains("numero de clientes") || lower.contains("clientes emisores")) {
                        nClientes = Integer.parseInt(extractNumber(line));
                    } else if (lower.contains("mensajes por cliente") || lower.contains("mensajes por cliente:") || lower.matches(".*\\bmensajes\\b.*")) {
                        mensajesPorCliente = Integer.parseInt(extractNumber(line));
                    } else if ((lower.contains("filtros") && lower.contains("spam")) || lower.contains("filtros de spam") || (lower.contains("filtros") && !lower.contains("serv"))) {
                        nFiltros = Integer.parseInt(extractNumber(line));
                    } else if (lower.contains("servidores") || lower.contains("servidores de entrega")) {
                        nServidores = Integer.parseInt(extractNumber(line));
                    } else if (lower.contains("capacidad") && lower.contains("entrada")) {
                        capacidadBuzonEntrada = Integer.parseInt(extractNumber(line));
                    } else if (lower.contains("capacidad") && lower.contains("entrega")) {
                        capacidadBuzonEntrega = Integer.parseInt(extractNumber(line));
                    }
                } catch (NumberFormatException ex) {
                    System.err.println("[Main] ERROR: No pude parsear número en la línea: " + line);
                    System.exit(1);
                }
            }
        } catch (IOException e) {
            System.err.println("[Main] ERROR leyendo el archivo: " + e.getMessage());
            System.exit(1);
        }

        if (nClientes == null || mensajesPorCliente == null || nFiltros == null ||
            nServidores == null || capacidadBuzonEntrada == null || capacidadBuzonEntrega == null) {
            System.err.println("[Main] ERROR: El archivo de configuración no contiene todos los parámetros requeridos.");
            System.err.println("  Parámetros requeridos (ejemplo en archivo):");
            System.err.println("    Número de clientes emisores: 2");
            System.err.println("    Número de mensajes por cliente: 3");
            System.err.println("    Número de filtros de spam: 5");
            System.err.println("    Número de servidores de entrega: 5");
            System.err.println("    Capacidad máxima del buzón de entrada: 10");
            System.err.println("    Capacidad del buzón de entrega: 10");
            System.exit(1);
        }

        if (nClientes <= 0 || mensajesPorCliente <= 0 || nFiltros <= 0 || nServidores <= 0 ||
            capacidadBuzonEntrada < 0 || capacidadBuzonEntrega < 0) {
            System.err.println("[Main] ERROR: Valores inválidos en configuración (deben ser positivos; capacidades >= 0).");
            System.exit(1);
        }

        System.out.println("[Main] Parámetros leídos:");
        System.out.println("  clientes emisores = " + nClientes);
        System.out.println("  mensajes por cliente = " + mensajesPorCliente);
        System.out.println("  filtros spam = " + nFiltros);
        System.out.println("  servidores entrega = " + nServidores);
        System.out.println("  capacidad buzón entrada = " + capacidadBuzonEntrada);
        System.out.println("  capacidad buzón entrega = " + capacidadBuzonEntrega);

        int totalMensajesGlobal = nClientes * mensajesPorCliente;
        BuzonEntrada buzonEntrada = new BuzonEntrada(capacidadBuzonEntrada, totalMensajesGlobal);
        BuzonCuarentena buzonCuarentena = new BuzonCuarentena();
        BuzonEntrega buzonEntrega = new BuzonEntrega(capacidadBuzonEntrega);

        Emisor.setTotalCorreosProducir(mensajesPorCliente);
        FiltroSpam.setTotalClientes(nClientes);

        Thread[] emisoresThreads = new Thread[nClientes];
        for (int i = 0; i < nClientes; i++) {
            Emisor e = new Emisor("Emisor-" + (i + 1), buzonEntrada);
            emisoresThreads[i] = e;
            e.start();
        }

        Thread[] filtrosThreads = new Thread[nFiltros];
        for (int i = 0; i < nFiltros; i++) {
            FiltroSpam filtro = new FiltroSpam(buzonEntrada, buzonCuarentena, buzonEntrega);
            filtrosThreads[i] = filtro;
            filtro.setName("FiltroSpam-" + (i + 1));
            filtro.start();
        }

        ManejadorCuarentena gestor = new ManejadorCuarentena(buzonCuarentena, buzonEntrega, 10);
        gestor.start();

        Thread[] servidoresThreads = new Thread[nServidores];
        for (int i = 0; i < nServidores; i++) {
            ServidorEntrega servidor = new ServidorEntrega(i + 1, buzonEntrega);
            servidoresThreads[i] = servidor;
            servidor.start();
        }

        for (Thread em : emisoresThreads) {
            try {
                if (em != null) em.join();
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        System.out.println("[Main] Todos los emisores terminaron de producir.");

        final long MAX_WAIT_MS = 300_000;
        final long CHECK_INTERVAL = 500;
        long waited = 0;
        long lastLog = 0;
        while (waited < MAX_WAIT_MS) {
            int cuarentenaCount = buzonCuarentena.tamano();
            boolean entregaVacia = buzonEntrega.estaVacio();

            if (cuarentenaCount == 0 && entregaVacia) {
                System.out.println("[Main] Cuarentena y buzón de entrega están vacíos. Procediendo a terminar.");
                break;
            }

            if (System.currentTimeMillis() - lastLog > 2000) {
                System.out.println("[Main] Esperando: cuarentena=" + cuarentenaCount + " correos, entregaVacia=" + entregaVacia + ", waited=" + waited + "ms");
                lastLog = System.currentTimeMillis();
            }

            try {
                Thread.sleep(CHECK_INTERVAL);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            waited += CHECK_INTERVAL;
        }
        if (waited >= MAX_WAIT_MS) {
            System.out.println("[Main] Tiempo máximo de espera alcanzado; procediendo a terminar de todos modos.");
        }

        gestor.detener();

        for (int i = 0; i < nServidores; i++) {
            Correo fin = new Correo("Finalizado");
            fin.setTipoMensaje(1);
            buzonEntrega.ponerCorreo(fin);
        }

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        for (Thread filtroThread : filtrosThreads) {
            try {
                if (filtroThread != null) filtroThread.join(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }
        for (Thread servidorThread : servidoresThreads) {
            try {
                if (servidorThread != null) servidorThread.join(2000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("[Main] Sistema terminado. Saliendo.");
        System.exit(0);
    }

    private static String extractNumber(String s) {
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (Character.isDigit(c)) {
                num.append(c);
            } else if (num.length() > 0) {
                break;
            }
        }
        return num.length() == 0 ? "0" : num.toString();
    }
}
