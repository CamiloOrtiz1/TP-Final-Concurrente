public class MontañaRusa implements Runnable {

    private final ControlMontañaRusa control;
    private volatile boolean abierto;
    private final int capacidadAsientos = 5;
    private final int capacidadSalaEspera = 10;

    public MontañaRusa(ControlMontañaRusa control) {
        this.control = new ControlMontañaRusa(capacidadAsientos, capacidadSalaEspera);
        this.abierto = true;
    }

    public void entrar(Object visitante) {
        if (!abierto) {
            System.out.println(visitante.toString() + " no puede entrar, la montaña rusa está cerrada.");
            return;
        }
        control.entrarMontañaRusa(visitante);
    }

    public void salir(Object visitante) {
        control.salirMontañaRusa(visitante);
    }

    public void cerrar() {
        abierto = false;
        control.cerrarMontañaRusa();
    }

    @Override
    public void run() {
        while (abierto || control.hayVisitantes()) {
            control.prepararRecorrido();
            control.iniciarRecorrido();

            try {
                Thread.sleep(4000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            control.finalizarRecorrido();
            control.descargarVisitantes();
        }
    }
}