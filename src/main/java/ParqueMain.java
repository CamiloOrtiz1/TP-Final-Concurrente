import entidades.Visitante;
import entidades.Encargado;
import java.util.ArrayList;
import java.util.List;

public class ParqueMain {
    public static void main(String[] args) {
        // 1. Inicialización de la atracción de Realidad Virtual (Semáforos Generales)
        // Stock inicial: 3 visores, 6 manoplas, 3 bases [cite: 56, 58]
        RealidadVirtual rv = new RealidadVirtual(3, 6, 3);
        
        // 2. Crear y lanzar el hilo del Encargado de VR
        Encargado encargadoVR = new Encargado("Jorge (Encargado VR)");
        Thread hiloEncargado = new Thread(() -> {
            try {
                rv.darEquipo(encargadoVR);
            } catch (InterruptedException e) {
                System.out.println("El encargado de VR ha terminado su turno.");
            }
        });
        hiloEncargado.setDaemon(true); // Se cierra cuando termine el programa
        hiloEncargado.start();

        // 3. Simulación de llegada de Visitantes
        System.out.println("--- El Parque abre sus puertas (09:00) --- [cite: 65]");
        
        List<Thread> hilosVisitantes = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            Visitante v = new Visitante("Visitante-" + i);
            Thread t = new Thread(() -> {
                try {
                    // El visitante intenta entrar a la actividad de Realidad Virtual [cite: 56]
                    rv.usarRealidadVirtual(v);
                    Thread.sleep(2000); // Simula el tiempo que el visitante pasa usando el equipo de realidad virtual
                    rv.devolverEquipo(v);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
            hilosVisitantes.add(t);
            t.start();
            
            // Los visitantes llegan de forma escalonada
            try { Thread.sleep(500); } catch (InterruptedException ignored) {}
        }

        // 4. Control del tiempo (Simulación de cierre) [cite: 66]
        try {
            // Esperamos a que los hilos terminen sus actividades
            for (Thread t : hilosVisitantes) {
                t.join(10000); // Timeout de seguridad
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println("--- Las actividades cierran (19:00) --- [cite: 66]");
        System.out.println("--- El parque queda vacío (23:00) --- [cite: 66]");
    }
}