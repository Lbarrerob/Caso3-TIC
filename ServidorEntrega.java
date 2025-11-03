import java.util.Random;

public class ServidorEntrega extends Thread{
    private Random rnd;
    private boolean running = true;
    private BuzonEntrega buzonEntrega;

    public ServidorEntrega(String name, BuzonEntrega bEntrega){
        super(name);
        this.rnd = new Random();
        this.buzonEntrega = bEntrega;
    }

    @Override
    public void run() {
        while (running) {
            Correo c = buzonEntrega.sacarCorreo();
            if (c != null) {
                if ("Finalizado".equals(c.getId())) {
                    System.out.println("[" + getName() + "] recibió FIN -> finaliza servidor");
                    running = false;
                } else {
                    try {
                        Thread.sleep(50 + rnd.nextInt(300));
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.println("[" + getName() + "] procesó " + c.getId());
                }
        }
    }
        System.out.println("[" + getName() + "] terminado");
    }
}
