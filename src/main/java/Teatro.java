import entidades.Visitante;

public class Teatro {

    private static final int CAPACIDAD_TEATRO = 20;
    private static final int INGRESO_VISITANTES = 5; 
    private int[] grupos;
    private int[] visitantesPorGrupo;
    private int visitantesActuales;

    public Teatro() {
        this.grupos = new int[CAPACIDAD_TEATRO / INGRESO_VISITANTES];
        this.visitantesPorGrupo = new int[INGRESO_VISITANTES];
        this.visitantesActuales = 0;
        for (int i = 0; i < grupos.length; i++) {
            grupos[i] = 0;
        }
        for (int i = 0; i < visitantesPorGrupo.length; i++) {
            visitantesPorGrupo[i] = 0;
        }
    }

    public void ingresarVisitante(Visitante visitante) {

        synchronized (this) {
            while (visitantesActuales >= CAPACIDAD_TEATRO) {
                try {
                    System.err.println("Teatro lleno. Visitante " + visitante.getNombre() + " esperando para ingresar.");
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                visitantesActuales++;
                int grupoIndex = visitantesActuales / INGRESO_VISITANTES;
                int visitanteIndex = visitantesActuales % INGRESO_VISITANTES;
                grupos[grupoIndex]++;
                visitantesPorGrupo[visitanteIndex]++;
                System.out.println("Visitante " + visitante.getNombre() + " ha ingresado al teatro. Total visitantes: " + visitantesActuales);
                notifyAll();           
            }    
        }
    }

    public void ingresarTeatro(Encargado encargado) {

        synchronized (this) {
            while (visitantesActuales < CAPACIDAD_TEATRO) {
                try {
                    System.out.println("Encargado " + encargado.getNombre() + " esperando a que el teatro se llene.");
                    wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
            System.out.println("Encargado " + encargado.getNombre() + " ha ingresado al teatro con " + visitantesActuales + " visitantes.");
            notifyAll();
        }
    }
}
