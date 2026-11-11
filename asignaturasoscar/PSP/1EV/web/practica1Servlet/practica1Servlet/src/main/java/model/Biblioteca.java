package model;

import java.util.ArrayList;
import java.util.List;

public class Biblioteca {
    private String nombre;
    private int capacidad;
    private List<Libro> coleccion;

    public Biblioteca(String nombre, int capacidad) {
        this.nombre = nombre;
        this.capacidad = capacidad;
        coleccion = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public void addLibro(Libro libro) {
        if (coleccion.size() < capacidad) {
            coleccion.add(libro);
        }
    }

    public Libro buscarLibro(String titulo) {
        for (Libro libro : coleccion) {
            if (libro.getTitulo().equalsIgnoreCase(titulo)) {
                return libro;
            }
        }
        return null;
    }

    public int getNumLibros() {
        return coleccion.size();
    }


}
