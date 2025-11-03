import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    private static int nEmisores;
    private static int nCorreosEmisor;
    private static int nFiltros;
    private static int nServidores;

    private static int capacidadEntrada;
    private static int capacidadEntrega;

    public static void main(String[] args) {
        
        // Leer el archivo de configuración
        try (BufferedReader br= new BufferedReader(new FileReader("./ParamConfigurables.txt"))) {
            String linea;
            
            while((linea= br.readLine())!= null){
                String nombre= linea.split(":")[0].trim();
                int val= Integer.parseInt(linea.split(":")[1].trim());
                
                switch (nombre) {
                        case "Número de clientes emisores":
                            nEmisores = val;
                            break;
                        case "Número de mensajes por cliente":
                            nCorreosEmisor = val;
                            break;
                        case "Número de filtros de spam":
                            nFiltros = val;
                            break;
                        case "Número de servidores de entrega":
                            nServidores = val;
                            break;
                        case "Capacidad máxima del buzón de entrada":
                            capacidadEntrada = val;
                            break;
                        case "Capacidad del buzón de entrega":
                            capacidadEntrega= val;
                            break;
                    }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Total de correos a producir 
        int totalCorreos = nEmisores*nCorreosEmisor;

        //Crear Buzones
        BuzonEntrada bEntrada = new BuzonEntrada(capacidadEntrada, totalCorreos);
        BuzonCuarentena bCuarentena = new BuzonCuarentena();
        BuzonEntrega bEntrega = new BuzonEntrega(capacidadEntrega);


        //Threads

            //Emisores
        for (int i= 0; i<nEmisores ;i++){
           Emisor e = new Emisor("Emisor_"+i, bEntrada);
           Emisor.setTotalCorreosProducir(totalCorreos);
           e.start();
        }
            //Filtros Spam
        for (int i= 0; i<nFiltros ;i++){
            FiltroSpam f= new FiltroSpam(bEntrada, bCuarentena, bEntrega);
            FiltroSpam.setTotalClientes(nEmisores);
            f.start();
        }
            /*Servidores de Entrega
        for (int i= 0; i<nServidores ;i++){
            ServidorEntrega s= new ServidorEntrega(BEN);
            s.start();
        }*/
            //Manejadores de Cuarentena
        ManejadorCuarentena m = new ManejadorCuarentena(bCuarentena, bEntrega);
        m.start();

    
    }

}


