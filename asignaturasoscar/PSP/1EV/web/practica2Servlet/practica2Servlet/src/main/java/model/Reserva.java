package model;

public class Reserva {
    private String nombreCliente;
    private Pelicula pelicula;
    private int numeroEntradas;

    public Reserva(String nombreCliente, Pelicula pelicula, int numeroEntradas) {
        this.nombreCliente = nombreCliente;
        this.pelicula = pelicula;
        this.numeroEntradas = numeroEntradas;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }

    public Pelicula getPelicula() {
        return pelicula;
    }

    public void setPelicula(Pelicula pelicula) {
        this.pelicula = pelicula;
    }

    public int getNumeroEntradas() {
        return numeroEntradas;
    }

    public void setNumeroEntradas(int numeroEntradas) {
        this.numeroEntradas = numeroEntradas;
    }
}
