package model;

public class Pelicula {
    private String titulo;
    private String horario;
    private int aforo;

    public Pelicula(String titulo, String horario, int aforo) {
        this.titulo = titulo;
        this.horario = horario;
        this.aforo = aforo;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getHorario() {
        return horario;
    }

    public void setHorario(String horario) {
        this.horario = horario;
    }

    public int getAforo() {
        return aforo;
    }

    public void setAforo(int aforo) {
        this.aforo = aforo;
    }
}
