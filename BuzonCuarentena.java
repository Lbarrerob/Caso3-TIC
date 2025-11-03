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

    /*
    public synchronized Correo sacarCorreo(){
    
    }
     */
}
