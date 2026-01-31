package entidades;

public class Visitante {

    private final String nombre;
    private int fichas;

    public Visitante(String nombre) {
        this.nombre = nombre;
        this.fichas = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getFichas() {
        return fichas;
    }
}
