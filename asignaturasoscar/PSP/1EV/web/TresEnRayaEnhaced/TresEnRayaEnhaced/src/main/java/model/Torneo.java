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

    public int getVictorias(String jugador){
        int victorias=0;
        for(int i=0;i<partidas.size();i++){
            if(partidas.get(i).getGanador().equals(jugador)){
                victorias++;
            }
        }
        return victorias;
    }

    public int getDerrotas(String jugador){
        int derrotas=0;
        for(int i=0;i<partidas.size();i++){
            if(partidas.get(i).getGanador()!=null && !partidas.get(i).getTurno().equals(jugador)){
                derrotas++;
            }
        }
        return derrotas;
    }

    public List <Partida> getPartidasByPlayer(String jugador){
        return partidas.stream().filter(partida -> partida.getJugador1().equals(jugador) || partida.getJugador2().equals(jugador)).toList();
    }

}
