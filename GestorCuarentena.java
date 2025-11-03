import java.util.List;
import java.util.Random;


public class GestorCuarentena extends Thread {
    private final BuzonCuarentena buzonCuarentena;
    private final BuzonEntrega buzonEntrega;
    private final int intervalo;
    private final Random rnd = new Random();
    private volatile boolean running = true;

    public GestorCuarentena(BuzonCuarentena buzonCuarentena, BuzonEntrega buzonEntrega, int intervaloMs) {
        super("GestorCuarentena");
        this.buzonCuarentena = buzonCuarentena;
        this.buzonEntrega = buzonEntrega;
        this.intervalo = Math.max(1, intervaloMs);
    }

    public void detener() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervalo);
            } catch (InterruptedException e) {
                if (!running) break;
            }

            buzonCuarentena.decrementarTodos(intervalo);

            List<Correo> listos = buzonCuarentena.sacarCorreo();

            // Para cada listo, decidir descartar al azar % 7 == 0, o mover a entrega
            for (Correo c : listos) {
                int azar = rnd.nextInt(21) + 1;
                if (azar % 7 == 0) {
                    System.out.println("[" + getName() + "] descartó " + c.getId() + " (azar=" + azar + ")");
                } else {
                    // mover a entrega
                    buzonEntrega.ponerCorreo(c);
                    System.out.println("[" + getName() + "] movió a entrega " + c.getId() + " (azar=" + azar + ")");
                }
            }
        }
        System.out.println("[" + getName() + "] terminado");
    }
}
