import java.util.LinkedList;
import java.util.Queue;

public class BuzonEntrega {
    private int capacidad;
    private Queue<Correo> correos;

    public BuzonEntrega(int capacidad){
        this.capacidad = capacidad;
        this.correos = new LinkedList<Correo>();
    }

    public synchronized void ponerCorreo(Correo correo){
        while (capacidad > 0 && tamaño() == capacidad){
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
        correos.add(correo);
        notifyAll();
    }

    public synchronized Correo sacarCorreo() {
        while (estaVacio()) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }
        }
        Correo c = correos.poll();
        notifyAll();
        return c;
    }

    public synchronized int tamaño() {
        return correos.size();
    }

    public synchronized boolean estaVacio() {
        return correos.isEmpty();
    }

}
