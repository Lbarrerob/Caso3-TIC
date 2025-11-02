public class Emisor extends Thread {
    private static int totalCorreosProducir;
    private int correosProducidos = 0;
    private BuzonEntrada buzonEntrada;

    public Emisor(String name, BuzonEntrada bEntrada){
        super(name);
        this.buzonEntrada = bEntrada;
    }

    @Override
    public void run(){

    }

    private Correo generarCorreo(){
        Correo correo = new Correo(this.getName()+" - Correo "+(correosProducidos));
        
        if (correosProducidos == 0) {
            correo.setTipoMensaje(0);
        } else if (correosProducidos == totalCorreosProducir){
            correo.setTipoMensaje(1);
        } else {
            correo.setTipoMensaje(2);
        }
        correosProducidos++;
        return correo;
    }

    public static void setTotalToProduce(int total){
        totalCorreosProducir = total;
    }
}
