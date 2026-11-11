package model;

public class Partida {

    private String jugador1;
    private String jugador2;

    private String marca1;
    private String marca2;

    private String turno;

    private int movimientos;

    private String ganador;

    private String [] tablero;

    private boolean finPartida;

    public Partida(String jugador1, String marca1, String jugador2,  String marca2) {
        this.jugador1 = jugador1;
        this.jugador2 = jugador2;
        this.marca1 = marca1;
        this.marca2 = marca2;
        if (Math.random() > 0.5) {
            turno = jugador1;
        } else {
            turno = jugador2;
        }
        movimientos = 0;
        finPartida = false;
        tablero = null;
        ganador = null;
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

    public String getMarca1() {
        return marca1;
    }

    public void setMarca1(String marca1) {
        this.marca1 = marca1;
    }

    public String getMarca2() {
        return marca2;
    }

    public void setMarca2(String marca2) {
        this.marca2 = marca2;
    }

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    public int getMovimientos() {
        return movimientos;
    }

    public void setMovimientos(int movimientos) {
        this.movimientos = movimientos;
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

    public String[] getTablero() {
        return tablero;
    }

    public void setTablero(String[] tablero) {
        this.tablero = tablero;
    }

    public void incrementMovimientos(){
        movimientos++;
    }
}
