package model;

import java.util.ArrayList;
import java.util.List;

public class Torneo {

    private List<Partida> partidas;

    public Torneo() {
        partidas = new ArrayList<Partida>();
    }

    public List<Partida> getPartidas() {
        return partidas;
    }

    public void setPartidas(List<Partida> partidas) {
        this.partidas = partidas;
    }

    public void addPartida(Partida partida){
        partidas.add(partida);
    }
}
