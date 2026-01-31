import entidades.*;
import java.util.concurrent.Exchanger;

public class AreaJuegos {

    private final Exchanger<String> ficha;
    
    public AreaJuegos() {
        this.ficha = new Exchanger<String>();
    }

    public void darFicha(Visitante visitante, Encargado encargado) {
        try {
            ficha.exchange(visitante.getNombre() + " Cambia una ficha al " + encargado.getNombre());
            int puntos = (int) (Math.random() * 10) * 100;
            Thread.sleep(puntos); 
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupción al dar ficha.");
        }
    }

    public void recibirFicha(Encargado encargado, Visitante visitante) {
        try {
            String mensaje = ficha.exchange(encargado.getNombre() + " Recibe una ficha de " + visitante.getNombre());
            System.out.println(mensaje);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Interrupción al recibir ficha.");
        }
    }

    public void recibirPremio(Visitante visitante) {
        String premio;
        int probabilidad = (int) (Math.random() * 100);
        if (probabilidad < 50) {
            premio = "Pelota";
        } else if (probabilidad < 80) {
            premio = "Muñeco de peluche";
        } else {
            premio = "Bicicleta";
        }
        System.out.println(visitante.getNombre() + " ha ganado una " + premio + "!");
    }
}
