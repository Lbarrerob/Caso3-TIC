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
        while (correos.size()==capacidad){
            Thread.yield();
        }
        correos.add(correo);
    }

    
    public synchronized Correo sacarCorreo(){
        while()
    }
    
}
