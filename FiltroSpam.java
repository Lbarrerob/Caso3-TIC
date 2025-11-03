import java.util.Random;

public class FiltroSpam extends Thread{
    private static int totalClientes;
    private int numInicios=0;
    private int numFins=0;
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
        while ((numInicios <= totalClientes) || (numFins != numInicios)) {
            Correo c = buzonEntrada.sacarCorreo();

            if (analizarParaSpam(c)){
                buzonCuarentena.ponerCorreo(c);
            } else {
                if(c.getTipoMensaje() == Tipo.INICIO){
                    numInicios++;
                } else if (c.getTipoMensaje() == Tipo.FIN){
                    numFins++;
                }
                buzonEntrega.ponerCorreo(c);
            }
        }
        
        if(!finalizado){
            Correo mensajeFin = new Correo("Finalizado");
            mensajeFin.setTiempo(20002);

        //manda mensaje de finalización a Cuarentena y Entrega
        buzonCuarentena.ponerCorreo(mensajeFin);
        buzonEntrega.ponerCorreo(mensajeFin);
        finalizado = true;
        
        }
    }

    private boolean analizarParaSpam(Correo correo){
        boolean esSpam = false;

        if(!correo.getFlagSpam()){
            Random t = new Random();
            int tiempo = t.nextInt(10001)+10000;
            
            correo.setTiempo(tiempo);
            esSpam = true;
        }
        return esSpam;
    }

    public static void setTotalClientes(int total){
        totalClientes = total;
    }
    
}
