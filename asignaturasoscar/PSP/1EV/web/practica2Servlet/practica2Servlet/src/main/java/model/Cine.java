package model;

import java.util.ArrayList;
import java.util.List;

public class Cine {
    private String nombreCine;
    private List<Pelicula> peliculas;
    private List<Reserva> reservas;

    public Cine(String nombreCine) {
        this.nombreCine = nombreCine;
        peliculas = new ArrayList<>();
        reservas = new ArrayList<>();
    }

    public String getNombreCine() {
        return nombreCine;
    }

    public void setNombreCine(String nombreCine) {
        this.nombreCine = nombreCine;
    }

    public Pelicula buscarPelicula(String titulo, String horario){
        for (Pelicula pelicula : peliculas){
            if (titulo.equals(pelicula.getTitulo()) && horario.equals(pelicula.getHorario())){
                return pelicula;
            }
        }
        return null;
    }

    public boolean addPelicula(Pelicula pelicula){
        if(buscarPelicula(pelicula.getTitulo(), pelicula.getHorario()) == null){
            peliculas.add(pelicula);
            return true;
        }
        return false;
    }

    public Reserva buscarReserva(String nombreCliente, Pelicula pelicula){
        for (Reserva reserva : reservas){
            if (nombreCliente.equals(reserva.getNombreCliente())&& pelicula.equals(reserva.getPelicula())){
                return reserva;
            }
        }
        return null;
    }

    public int getEntradas(Pelicula pelicula){
        int numEntradas = 0;
        for (Reserva reserva : reservas){
            if (pelicula.equals(reserva.getPelicula())){
                numEntradas+=reserva.getNumeroEntradas();
            }
        }
        return numEntradas;
    }

    public boolean addReserva(Reserva reserva){
        if(getEntradas(reserva.getPelicula())+reserva.getNumeroEntradas()<=reserva.getPelicula().getAforo()){
            reservas.add(reserva);
            return true;
        }
        return false;
    }

    public boolean removeReserva(String nombreCliente, Pelicula pelicula){
        Reserva reserva = buscarReserva(nombreCliente, pelicula);
        if(reserva!=null){
        reservas.remove(reserva);
        return true;
        }
        return false;
    }

    public int getNumPeliculas(){
        return peliculas.size();
    }

    public int getNumReservas(){
        return reservas.size();
    }

    public int getNumEntradasReservadas(){
        int numEntradas = 0;
        for (Reserva reserva : reservas){
            numEntradas+=reserva.getNumeroEntradas();
        }
        return numEntradas;
    }


}
