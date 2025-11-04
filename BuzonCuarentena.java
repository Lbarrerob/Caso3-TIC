// BuzonCuarentena.java
import java.util.LinkedList;
import java.util.Queue;
import java.util.Iterator;

public class BuzonCuarentena {
    private final Queue<Correo> correos;

    public BuzonCuarentena(){
        this.correos = new LinkedList<Correo>();
    }

    // Pone correo en cuarentena
    public synchronized void ponerCorreo(Correo correo) {
        correos.add(correo);
        System.out.println("[BuzonCuarentena] Se agregó a cuarentena: " + correo.getId() +
                           " (tiempo=" + correo.getTiempo() + ", spam=" + correo.getFlagSpam() + ")");
        notify();
    }

    // Decrementa el tiempo de todos los correos en la caja (delta en ms)

    public synchronized Queue<Correo> sacarCorreos(){
        while (correos.size() != 0) {
            Thread.yield();
        }

        //decrementa en 1 le tiempo de todos los correos en cuarentena
        for (Correo c : correos) {
            c.decrementarTiempo();
        }

        Queue<Correo> listos = new LinkedList<>();
        Iterator<Correo> it = correos.iterator();
        while (it.hasNext()){
            Correo c = it.next();
            if (c.getTiempo() <= 0) {
                listos.add(c);
                it.remove();
            }
        }
        return listos;
    }

    public synchronized boolean estaVacia() {
        return correos.isEmpty();
    }

    public synchronized int tamano() {
        return correos.size();
    }
}
