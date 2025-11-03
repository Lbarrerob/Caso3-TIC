import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class BuzonCuarentena {
    private Queue<Correo> correos;

    public BuzonCuarentena(){
        this.correos = new LinkedList<Correo>();
    }

    public synchronized void ponerCorreo(Correo correo){
        correos.add(correo);
    }
    
    public synchronized Queue<Correo> sacarCorreosAlManejador(){
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

    
}
