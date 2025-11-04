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
        notifyAll();
    }

    // Decrementa el tiempo de todos los correos en la caja (delta en ms)
    public synchronized void decrementarTodos(int delta) {
        if (correos.isEmpty()) return;
        int count = correos.size();
        for (Correo c : correos) {
            int t = c.getTiempo();
            int nuevo = Math.max(0, t - delta);
            c.setTiempo(nuevo);
        }
        // Informe resumido
        System.out.println("[BuzonCuarentena] decrementarTodos(" + delta + "): " + count + " correos en cuarentena (tiempos actualizados).");
        notifyAll();
    }

    public synchronized Queue<Correo> sacarCorreos() {

        if (correos.isEmpty()) {
            return new LinkedList<>();
        }

        Queue<Correo> listos = new LinkedList<>();
        Iterator<Correo> it = correos.iterator();
        while (it.hasNext()) {
            Correo c = it.next();
            if (c.getTiempo() <= 0) {
                listos.add(c);
                it.remove();
            }
        }

        if (!listos.isEmpty()) {
            System.out.println("[BuzonCuarentena] " + listos.size() + " correos listos para salir de cuarentena.");
            for (Correo c : listos) {
                System.out.println("  -> Listo: " + c.getId() + " (tiempo=" + c.getTiempo() + ")");
            }
        } else {
        }

        notifyAll();
        return listos;
    }

    public synchronized boolean estaVacia() {
        return correos.isEmpty();
    }

    public synchronized int tamano() {
        return correos.size();
    }
}
