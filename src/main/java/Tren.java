
import entidades.Visitante;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Tren {

    private static final int CAPACIDAD_TREN = 10;
    private final BlockingQueue<Visitante> pasajeros;
    private CountDownLatch finViajeLatch;

    public Tren() {
        this.pasajeros = new ArrayBlockingQueue<>(CAPACIDAD_TREN, true);
        this.finViajeLatch = new CountDownLatch(1);
    }

    public void subir(Visitante visitante) throws InterruptedException {
        pasajeros.put(visitante);
        System.out.println(visitante.getNombre() + " ha subido al tren.");
        CountDownLatch finViajeLatchActual = this.finViajeLatch;
        finViajeLatchActual.await();
        System.err.println(visitante.getNombre() + " ha bajado del tren.");
    }

    public void iniciarViaje() throws InterruptedException {
        List<Visitante> pasajerosDelViaje = new ArrayList<>();
        long tiempoInicio = System.currentTimeMillis();
        long tiempoMaximoEspera = 5000; // 5 "Minutos" en milisegundos"

        System.out.println("--- TREN: Esperando pasajeros para iniciar el viaje (Máximo 5 segundos) ---");
        while (pasajerosDelViaje.size() < CAPACIDAD_TREN) {

            long tiempoRestante = tiempoMaximoEspera - (System.currentTimeMillis() - tiempoInicio);
            if (tiempoRestante <= 0) {
                System.out.println("--- TREN: Tiempo de espera agotado. Iniciando viaje con " + pasajerosDelViaje.size() + " pasajeros ---");
                break;
            }

            Visitante visitante = pasajeros.poll(tiempoRestante, TimeUnit.MILLISECONDS);
            if (visitante != null) {
                pasajerosDelViaje.add(visitante);
                System.err.println("--- TREN: " + visitante.getNombre() + " ha subido al tren (" + pasajerosDelViaje.size() + "/" + CAPACIDAD_TREN + ") ---");
            }
        }

        if (pasajerosDelViaje.isEmpty()) {
            System.out.println("--- TREN: No hay pasajeros para el viaje. Cancelando viaje. ---");
        } else {
            System.out.println("--- TREN: Iniciando viaje con " + pasajerosDelViaje.size() + " pasajeros ---");
            Thread.sleep(3000);
            System.out.println("--- TREN: Viaje finalizado. Todos los pasajeros pueden bajar. ---");
            finViajeLatch.countDown();
            finViajeLatch = new CountDownLatch(1);
        }
    }
}
