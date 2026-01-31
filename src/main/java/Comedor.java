import entidades.Visitante;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Semaphore;

public class Comedor {

    private static final int CAPACIDAD_MESA = 4;
    private final CyclicBarrier[] mesas;
    private final int[] lugarMesa;
    private final Semaphore capacidadComedor;
    

    public Comedor(int cantMesas) {
        this.mesas = new CyclicBarrier[cantMesas];
        this.lugarMesa = new int[CAPACIDAD_MESA];
        for (int i = 0; i < cantMesas; i++) {
            final int numeroMesa = i;
            mesas[i] = new CyclicBarrier(CAPACIDAD_MESA, () -> {
                System.out.println("Mesa " + (numeroMesa + 1) + " Está llena. Los visitantes pueden comenzar a comer.");
            });
            lugarMesa[i] = 0;
        }
        this.capacidadComedor = new Semaphore(cantMesas * CAPACIDAD_MESA);
    }

    public void entrarComedor(Visitante visitante) throws BrokenBarrierException {
        if (!capacidadComedor.tryAcquire()) {
            System.out.println(visitante.getNombre() + " No pudo entrar al comedor. Está lleno.");
            return;
        }

        int numeroMesa = -1;
        synchronized (lugarMesa) {
            int i = 0;
            while (i < mesas.length && numeroMesa == -1) {
                if (lugarMesa[i] < CAPACIDAD_MESA) {
                    numeroMesa = i;
                    lugarMesa[i]++;
                    System.out.println(visitante.getNombre() + " Se ha sentado en la mesa " + (numeroMesa + 1) + ".");
                }
                i++;
            }
        }

        if (numeroMesa != -1) {
            try {
                mesas[numeroMesa].await();
                System.out.println(visitante.getNombre() + " Está comiendo en la mesa " + (numeroMesa + 1) + ".");
                Thread.sleep((int) (Math.random() * 3000) + 1000);
                System.out.println(visitante.getNombre() + " Ha terminado de comer en la mesa " + (numeroMesa + 1) + ".");
            } catch (InterruptedException | BrokenBarrierException e) {
                Thread.currentThread().interrupt();
                System.out.println(visitante.getNombre() + " Fue interrumpido mientras comía.");
            } finally {
                synchronized (lugarMesa) {
                    lugarMesa[numeroMesa]--;
                }
                capacidadComedor.release();
            }
        }
    }
}
