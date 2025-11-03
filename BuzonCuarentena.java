import java.util.LinkedList;
import java.util.Queue;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class BuzonCuarentena {
    private Queue<Correo> correos;

    public BuzonCuarentena(){
        this.correos = new LinkedList<Correo>();
    }

    // Pone correo en cuarentena
    public synchronized void ponerCorreo(Correo correo) {
        correos.add(correo);
        notifyAll();
    }

    // Decrementa el tiempo de todos los correos en la caja
    public synchronized void decrementarTodos(int delta) {
        for (Correo c : correos) {
            int t = c.getTiempo();
            c.setTiempo(Math.max(0, t - delta));
        }
    }


    // Extrae y devuelve la lista de correos cuyo tiempo <= 0, eliminándolos de la cola.
    public synchronized List<Correo> sacarCorreo() {
        List<Correo> listos = new ArrayList<>();
        Iterator<Correo> it = correos.iterator();
        while (it.hasNext()) {
            Correo c = it.next();
            if (c.getTiempo() <= 0) {
                listos.add(c);
                it.remove();
            }
        }
        notifyAll();
        return listos;
    }

    public synchronized boolean estaVacia() {
        return correos.isEmpty();
    }

    public synchronized int tamaño() {
        return correos.size();
    }
}
