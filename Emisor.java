public class Emisor extends Thread {
    private static int totalCorreosProducir;
    private int correosProducidos = 1;
    private BuzonEntrada buzonEntrada;

    public Emisor(String name, BuzonEntrada bEntrada){
        super(name);
        this.buzonEntrada = bEntrada;
    }

    @Override
    public void run(){
        for(int i=0; i<totalCorreosProducir; i++){
            Correo c = generarCorreo();
            buzonEntrada.ponerCorreo(c);
        }
    }

    private Correo generarCorreo(){
        Correo correo = new Correo("Correo_"+(correosProducidos)+"("+this.getName()+")");
        
        if (correosProducidos == 1) {
            correo.setTipoMensaje(0);
        } else if (correosProducidos == totalCorreosProducir){
            correo.setTipoMensaje(1);
        } else {
            correo.setTipoMensaje(2);
        }
        correosProducidos++;
        return correo;
    }

    public static void setTotalCorreosProducir(int total){
        totalCorreosProducir = total;
    }
}
