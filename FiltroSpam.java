import java.util.Random;

public class FiltroSpam extends Thread{
    private static int totalClientes;
    private static int numInicios=0;
    private static int numFins=0;
    private static boolean finalizado=false;

    private BuzonEntrada buzonEntrada;
    private BuzonCuarentena buzonCuarentena;
    private BuzonEntrega buzonEntrega;

    public FiltroSpam(BuzonEntrada bEntrada, BuzonCuarentena bCuarentena, BuzonEntrega bEntrega){
        this.buzonEntrada = bEntrada;
        this.buzonCuarentena = bCuarentena;
        this.buzonEntrega = bEntrega;
    }

    @Override
    public void run(){
        
        while ((numInicios < totalClientes) || (numFins < totalClientes)) {
            
            Correo c = buzonEntrada.sacarCorreo();
            System.out.println("Se sacó del buzón ENTRADA el " + c.getId());

            if (c!= null) {
                if (analizarParaSpam(c)){
                    System.out.println(c.getId()+" es Spam y se envia a CUARENTENA");
                    buzonCuarentena.ponerCorreo(c);

                } else {

                    if(c.getTipoMensaje() == Tipo.INICIO){
                        System.out.println(c.getId()+" NO es Spam y se envia a ENTREGA");
                        buzonEntrega.ponerCorreo(c);
                        numInicios++;
                    } else if (c.getTipoMensaje() == Tipo.FIN){
                        System.out.println(c.getId()+" NO es Spam y se envia a ENTREGA");
                        buzonEntrega.ponerCorreo(c);
                        numFins++;
                    } else {
                        System.out.println(c.getId()+" NO es Spam y se envia a ENTREGA");
                        buzonEntrega.ponerCorreo(c);
                    }
                }
            }
            System.out.println("atrapado");
            
        }
        if(!finalizado){
            Correo mensajeFin = new Correo("Finalizado");
            mensajeFin.setTiempo(200);

                //manda mensaje de finalización a Cuarentena y Entrega
            buzonCuarentena.ponerCorreo(mensajeFin);
            buzonEntrega.ponerCorreo(mensajeFin);
            System.out.println("Se envió el mensaje de señalamiento FINALIZADO a buzón CUARENTENA y ENTREGA");
            finalizado = true;
        
            }
        
    
    }

    private boolean analizarParaSpam(Correo correo){
        boolean esSpam = false;

        if(correo.getFlagSpam()){
            Random t = new Random();
            int tiempo = t.nextInt(100)+100;
            
            correo.setTiempo(tiempo);
            esSpam = true;
        }
        return esSpam;
    }

    public static void setTotalClientes(int total){
        totalClientes = total;
    }
    
}
