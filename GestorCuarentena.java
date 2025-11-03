import java.util.List;
import java.util.Random;


public class GestorCuarentena extends Thread {
    private final BuzonCuarentena caja;
    private final BuzonEntrega buzonEntrega;
    private final int intervaloMs;
    private final Random rnd = new Random();
    private volatile boolean running = true;

    public GestorCuarentena(BuzonCuarentena caja, BuzonEntrega buzonEntrega, int intervaloMs) {
        super("GestorCuarentena");
        this.caja = caja;
        this.buzonEntrega = buzonEntrega;
        this.intervaloMs = Math.max(1, intervaloMs);
    }

    public void detener() {
        running = false;
        this.interrupt();
    }

    @Override
    public void run() {
        while (running) {
            try {
                Thread.sleep(intervaloMs);
            } catch (InterruptedException e) {
                if (!running) break;
            }

            caja.decrementarTodos(intervaloMs);

            List<Correo> listos = caja.obtenerListoYLimpiar();

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
