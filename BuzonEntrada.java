import java.util.LinkedList;
import java.util.Queue;

public class BuzonEntrada {
    private int capacidad;
    private int totalCorreos;
    private Queue<Correo> correos;

    public BuzonEntrada(int capacidad, int totalCorreos){
        this.capacidad = capacidad;
        this.totalCorreos = totalCorreos;
        this.correos = new LinkedList<Correo>();
    }

    public synchronized void ponerCorreo(Correo correo){
        while (correos.size()==capacidad){
            try {
                wait();
            } catch (InterruptedException e) { }
        }
        correos.add(correo);
        totalCorreos--;
        notify();
    }

    public synchronized Correo sacarCorreo(){
        Correo c = null;
        while (correos.size()==0) {
            try {
                wait();
            } catch (InterruptedException e) { }   
        }

        if (totalCorreos >= 0) {
            c = correos.poll();
            notify();
        }
        return c;
    }
}
