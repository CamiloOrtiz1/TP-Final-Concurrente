package atracciones;

import entidades.Visitante;

public abstract class Atraccion {

    protected  String nombre;
    protected  boolean abierta;

    public Atraccion(String nombre) {
        this.nombre = nombre;
        this.abierta = true;
    }

    public String getNombre() {
        return nombre;
    }

    public boolean estaAbierta() {
        return abierta;
    }

    public abstract void entrar(Visitante visitante);

    public abstract void iniciarJuego();

    public void cerrar() {
        this.abierta = false;
        System.err.println("La atracción " + nombre + " está cerrada.");
    }

    protected void simularJuego(long duracion) {
        try {
            Thread.sleep(duracion);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
