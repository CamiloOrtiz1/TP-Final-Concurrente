package atracciones;

import entidades.Visitante;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MontaniaRusa extends Atraccion {

    private final Lock lock = new ReentrantLock();
    private final Condition esperaLleno = lock.newCondition();
    private final Condition esperaVacio = lock.newCondition();

    private int visitantesEspera = 0;
    private boolean enViaje = false;

    public MontaniaRusa() {
        super("Montaña Rusa");
    }

    @Override
    public void entrar(Visitante visitante) {
        lock.lock();
        try {
            if (!enViaje || visitantesEspera >= 5) {
                System.out.println(visitante.getNombre() + " está la fila llena de " + nombre);
                return;
            }
            visitantesEspera++;
            System.out.println(visitante.getNombre() + " ha entrado a " + nombre + ". Toma el asiento: " + visitantesEspera + "/5");

            if (visitantesEspera == 5) {
                System.err.println("Montaña Rusa llena. Iniciando el viaje...");
                esperaLleno.signal();
            }

            while (enViaje || visitantesEspera < 5) {
                esperaVacio.await();
            }

            System.out.println(visitante.getNombre() + " ha terminado el viaje en " + nombre);
            visitantesEspera--;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void iniciarJuego() {
        lock.lock();
        try {

            while (visitantesEspera < 5) {
                System.out.println("Esperando más visitantes para iniciar " + nombre);
                esperaLleno.await();
            }

            enViaje = true;
            System.out.println("Iniciando el viaje en " + nombre + " con 5 visitantes.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            lock.unlock();
        }

        simularJuego(4000); // Simula el viaje por 3 segundos

        lock.lock();
        try {
            enViaje = false;
            System.out.println("El viaje en " + nombre + " ha terminado.");
            esperaVacio.signalAll();
        } finally {
            lock.unlock();
        }
    }

}
