// ManejadorCuarentena.java
import java.util.Queue;
import java.util.Random;

public class ManejadorCuarentena extends Thread {
    private final BuzonCuarentena buzonCuarentena;
    private final BuzonEntrega buzonEntrega;
    private final Random rnd = new Random();
    private volatile boolean running = true;
    private int ciclos = 0;

    public ManejadorCuarentena(BuzonCuarentena buzonCuarentena, BuzonEntrega buzonEntrega, int intervaloMs) {
        super("ManejadorCuarentena");
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
    }

    public void detener() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {

            ciclos++;
            int antes = buzonCuarentena.tamano();
            if (antes > 0 && ciclos % 10 == 0) {
                System.out.println("[" + getName() + "] ciclo " + ciclos + ": " + antes + " correos en cuarentena antes del decremento.");
            }


            // Recuperar los correos que ya cumplieron su tiempo
            Queue<Correo> listos = buzonCuarentena.sacarCorreos();

            if (!listos.isEmpty()) {
                System.out.println("[" + getName() + "] ciclo " + ciclos + ": " + listos.size() + " correos listos para revisión.");
            }

            // Procesar cada correo listo
            for (Correo c : listos) {
                int azar = rnd.nextInt(21) + 1;
                if (azar % 7 == 0) {
                    System.out.println("[" + getName() + "] DESCARTÓ correo " + c.getId() + " (azar=" + azar + ")");
                } else {
                    buzonEntrega.ponerCorreo(c);
                    System.out.println("[" + getName() + "] MOVIÓ A ENTREGA correo " + c.getId() + " (azar=" + azar + ")");
                }
            }

            int despues = buzonCuarentena.tamano();
            if (antes != despues && ciclos % 10 == 0) {
                System.out.println("[" + getName() + "] después del ciclo " + ciclos + ": cuarentena ahora contiene " + despues + " correos.");
            }
        }

        System.out.println("[" + getName() + "] terminado tras " + ciclos + " ciclos.");
    }
}
