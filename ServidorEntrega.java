import java.util.Random;

public class ServidorEntrega extends Thread {
    private final int idServidor;
    private final BuzonEntrega buzonEntrega;
    private final Random rnd = new Random();

    public ServidorEntrega(int idServidor, BuzonEntrega buzonEntrega) {
        super("Servidor-" + idServidor);
        this.idServidor = idServidor;
        this.buzonEntrega = buzonEntrega;
    }

    @Override
    public void run() {
        while (true) {
            Correo c = buzonEntrega.sacarCorreo();
            if (c == null) break;

            if (c.getTipoMensaje() == Tipo.FIN || "Finalizado".equals(c.getId())) {
                System.out.println("[" + getName() + "] recibió FIN -> finaliza servidor");
                break;
            }

            try {
                Thread.sleep(50 + rnd.nextInt(300));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
            System.out.println("[" + getName() + "] procesó " + c.getId());
        }
        System.out.println("[" + getName() + "] terminado");
    }
}
