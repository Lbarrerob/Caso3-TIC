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
        System.out.println("[" + getName() + "] iniciado.");
        while (true) {
            Correo c = buzonEntrega.sacarCorreo();
            if (c == null) {
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
                continue;
            }

            if (c.getTipoMensaje() == Tipo.FIN || "Finalizado".equals(c.getId())) {
                System.out.println("[" + getName() + "] recibió FIN -> finaliza servidor");
                break;
            }

            System.out.println("[" + getName() + "] recibió " + c.getId() + " -> procesando...");
            try {
                Thread.sleep(50 + rnd.nextInt(300)); 
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }

            System.out.println("[" + getName() + "] procesó correctamente " + c.getId());
        }
        System.out.println("[" + getName() + "] terminado.");
    }
}
