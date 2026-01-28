import java.util.concurrent.Semaphore;

public class ControlMontañaRusa {

    private final Semaphore asientos;
    private final Semaphore salaEspera;

    private enum Estado {
        PREPARANDO,
        EN_RECORRIDO,
        FINALIZADO
    }

    private int visitantesEnMontaña;
    private Estado estado;
    private volatile boolean cerrado = false;
    private final Object estadoMontaña = new Object();

    public ControlMontañaRusa(int capacidadAsientos, int capacidadSalaEspera) {
        this.asientos = new Semaphore(capacidadAsientos, true);
        this.salaEspera = new Semaphore(capacidadSalaEspera, true);
        this.estado = Estado.PREPARANDO;
        this.cerrado = false;
        this.visitantesEnMontaña = 0;
 }

    public void entrarMontañaRusa(Object visitante) {
        
        if (!salaEspera.tryAcquire()) {
            System.out.println(visitante.toString() + " no puede entrar, la sala de espera está llena.");
            return;
        }

        try {
            asientos.acquire();
            this.visitantesEnMontaña++;
            salaEspera.release();
            System.out.println(visitante.toString() + " ha tomado asiento en la montaña rusa.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public void salirMontañaRusa(Object visitante) {
        asientos.release();
        visitantesEnMontaña--;
        System.out.println(visitante.toString() + " ha salido de la montaña rusa.");
    }

    public void prepararRecorrido() {
        synchronized (estadoMontaña) {
            estado = Estado.PREPARANDO;
            System.out.println("La montaña rusa se está preparando para el recorrido.");
        }
    }

    public void iniciarRecorrido() {
        synchronized (estadoMontaña) {
            estado = Estado.EN_RECORRIDO;
            System.out.println("La montaña rusa ha iniciado el recorrido.");
        }
    }

    public void finalizarRecorrido() {
        synchronized (estadoMontaña) {
            estado = Estado.FINALIZADO;
            System.out.println("La montaña rusa ha finalizado el recorrido.");
        }
    }

    public boolean hayVisitantes() {
        return this.visitantesEnMontaña == 5;
    }
}