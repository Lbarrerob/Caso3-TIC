import java.util.Random;

public class Correo {
    private String id;
    private boolean flagSpam;
    private Tipo tipoMensaje;
    private int tiempo;

    public Correo(String id){
        this.id = id;

        //Spam random
        Random i = new Random();
        int spam = i.nextInt(2);
        this.flagSpam = (spam==1);
    }

    public String getId(){
        return id;
    }
    public boolean getFlagSpam(){
        return flagSpam;
    }

    //para tipo de mensaje
    public void setTipoMensaje(int tipo){
        if (tipo == 0) {
            this.tipoMensaje = Tipo.INICIO;
            this.flagSpam = false;
        } else if (tipo == 1) {
            this.tipoMensaje = Tipo.FIN;
            this.flagSpam = false;
        } else if (tipo == 2) {
            this.tipoMensaje = Tipo.OTRO;
        }
    }
    public Tipo getTipoMensaje(){
        return tipoMensaje;
    }

    //para el tiempo en cuarentena
    public void setTiempo(int tiempo){
        this.tiempo = tiempo;
    }
    public void decrementarTiempo(){
        this.tiempo--;
    }
    public int getTiempo(){
        return tiempo;
    }
}