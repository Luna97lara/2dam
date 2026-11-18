package model;

import java.util.ArrayList;
import java.util.List;

public class Partida {

    private String jugador1;
    private String jugador2;

    private String letraJugador1;
    private String letraJugador2;

    private String turno;

    private String ganador;

    private String palabraSecreta;

    private boolean finPartida;

    private List<String> historial;


    public Partida(String jugador1, String jugador, String palabraSecreta) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador;
        this.palabraSecreta = palabraSecreta;
        if (Math.random() > 0.5) {
            turno = jugador1;
        } else {
            turno = jugador2;
        }
        finPartida = false;
        ganador = null;
        historial = new ArrayList<>();
    }

    public String getJugador1() {
        return jugador1;
    }

    public void setJugador1(String jugador1) {
        this.jugador1 = jugador1;
    }

    public String getJugador2() {
        return jugador2;
    }

    public void setJugador2(String jugador2) {
        this.jugador2 = jugador2;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public String getGanador() {
        return ganador;
    }

    public void setGanador(String ganador) {
        this.ganador = ganador;
    }

    public boolean isFinPartida() {
        return finPartida;
    }

    public void setFinPartida(boolean finPartida) {
        this.finPartida = finPartida;
    }

    public String getPalabraSecreta() {
        return palabraSecreta;
    }

    public void setPalabraSecreta(String palabraSecreta) {
        this.palabraSecreta = palabraSecreta;
    }

    public List<String> getHistorial() {
        return historial;
    }
}
