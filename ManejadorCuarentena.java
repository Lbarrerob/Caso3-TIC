import java.util.Queue;
import java.util.Random;

public class ManejadorCuarentena extends Thread {
    private Random rnd;
    private boolean running = true;
    
    private BuzonCuarentena buzonCuarentena;
    private BuzonEntrega buzonEntrega;

    public ManejadorCuarentena(BuzonCuarentena bCuarentena, BuzonEntrega bEntrega){
        this.rnd = new Random();
        this.buzonCuarentena = bCuarentena;
        this.buzonEntrega = bEntrega;
    }

    @Override
    public void run(){
        while(running){
            Queue<Correo> revision = buzonCuarentena.sacarCorreosAlManejador();
            for(Correo c : revision){
                if(descartarMalicioso(c)){
                    System.out.println("El manejador descartó " + c.getId());
                } else {
                    buzonEntrega.ponerCorreo(c);
                }
            }
        }
    }   
    
    private boolean descartarMalicioso(Correo c){
        boolean esMalicioso = false;
        int azar = rnd.nextInt(21) + 1;
        
        if (azar % 7 == 0) {
            esMalicioso = true;
        }
        return esMalicioso;
    }
}
