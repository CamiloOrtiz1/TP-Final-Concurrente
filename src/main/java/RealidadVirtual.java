import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import entidades.Visitante;
import entidades.Encargado;

public class RealidadVirtual {

    /*
     * El sistema de realidad virtual tiene 3 componentes: visor, manoplas y base.
     * Para utilizar el sistema de realidad virtual, un visitante debe adquirir los tres componentes (visor, manoplas y base) que le proporciona un encargado.
     * Si el encargado no tiene suficiente equipo para proporcionar, el visitante debe esperar hasta que el encargado tenga el equipo disponible. 
     * El encargado puede proporcionar el equipo a un visitante a la vez, y el visitante debe esperar hasta que el encargado le proporcione el equipo antes de poder usarlo.
     */

    // Exclusión mutua para proteger el acceso a la cola y el stock del equipo
    private final Semaphore mutex;
    // Semáforo para despertar al encargado
    private final Semaphore solicitudesEncargado;
    // Utilizo un contenedor para representar el turno de cada visitante (Semáforo) y el visitante asociado al turno
    private record Ticket(Visitante visitante, Semaphore turno) {}
    // Cola de semáforos para los visitantes que esperan por el equipo de realidad virtual
    private final Queue<Ticket> colaEsperaVisitantes;

    private int cantidadVisores, cantidadManoplas, cantidadBases;

    /*
     * El constructor de la clase RealidadVirtual inicializa los semáforos para controlar el acceso al equipo de realidad virtual. 
     * El semáforo mutex garantiza que el encargado controle y proporcione los recursos de manera segura, evitando que haya bloqueos.
     * Las cantidades de visores, manoplas y bases se inicializan según los parámetros proporcionados al constructor.
    */
    public RealidadVirtual(int cantidadVisores, int cantidadManoplas, int cantidadBases) {
        this.mutex = new Semaphore(1, true);
        this.solicitudesEncargado = new Semaphore(0, true);
        this.colaEsperaVisitantes = new LinkedList<>();
        this.cantidadVisores = cantidadVisores;
        this.cantidadManoplas = cantidadManoplas;
        this.cantidadBases = cantidadBases;
    }

    /*
     * El método usarRealidadVirtual simula el proceso de un visitante utilizando el equipo de realidad virtual.
     */
    public void usarRealidadVirtual(Visitante visitante) throws InterruptedException {
        // Cada visitante gestiona su propio turno con un semáforo individual
        Semaphore turno = new Semaphore(0);
        System.out.println(visitante.getNombre() + " ingresa al area de realidad virtual.");
        mutex.acquire();
        colaEsperaVisitantes.add(new Ticket(visitante, turno)); // El visitante se agrega a la cola de espera para usar el equipo de realidad virtual.
        System.out.println(visitante.getNombre() + " esta esperando para usar el equipo de realidad virtual que el encargado proporcione.");
        solicitudesEncargado.release(); // Se avisa al encargado que hay un visitante esperando para usar el equipo de realidad virtual.
        mutex.release();
        turno.acquire(); // El visitante espera a que el encargado le proporcione el equipo de realidad virtual.
        System.out.println(visitante.getNombre() + " ha recibido el equipo de realidad virtual.");
    }

    public void devolverEquipo(Visitante visitante) throws InterruptedException {
        mutex.acquire();
        cantidadVisores++;
        cantidadManoplas += 2;
        cantidadBases++;
        System.out.println(visitante.getNombre() + " devolvio el equipo de realidad virtual. Stock actual - Visores: " + cantidadVisores + ", Manoplas: " + cantidadManoplas + ", Bases: " + cantidadBases);
        solicitudesEncargado.release();
        mutex.release();
    }


    /*
     * El método darEquipo simula el proceso de un encargado proporcionando el equipo de realidad virtual a los visitantes.
     */
    public void darEquipo(Encargado encargado) throws InterruptedException {
        while (true) {
            solicitudesEncargado.acquire(); // El encargado espera a que haya un visitante esperando para usar el equipo de realidad virtual.
            mutex.acquire();
            if (!colaEsperaVisitantes.isEmpty() && cantidadVisores > 0 && cantidadManoplas > 1 && cantidadBases > 0) {
                cantidadVisores--;
                cantidadManoplas -= 2;
                cantidadBases--;
                Ticket ticket = colaEsperaVisitantes.poll();
                Visitante visitante = ticket.visitante();
                System.out.println(encargado.getNombre() + " proporciono el equipo de realidad virtual a " + visitante.getNombre() + ". Stock actual - Visores: " + cantidadVisores + ", Manoplas: " + cantidadManoplas + ", Bases: " + cantidadBases);
                ticket.turno().release(); // Se avisa al visitante que el equipo de realidad virtual está disponible.
            }
            mutex.release();
        }
    }
}
