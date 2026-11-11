package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Torneo {

    private List<Partida> partidas;

    private List <String> jugadores;

    public Torneo() {
        partidas = new ArrayList<Partida>();
        jugadores = new ArrayList<>();
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

    public List <Partida> getPartidasByPlayer(String jugador){
        return partidas.stream().filter(partida -> partida.getJugador1().equals(jugador) || partida.getJugador2().equals(jugador)).toList();
    }

    public void dropTorneo(String jugador){
        partidas=partidas.stream().filter(partida -> !partida.getJugador1().equals(jugador) && !partida.getJugador2().equals(jugador)).collect(Collectors.toCollection(ArrayList::new));
        jugadores=jugadores.stream().filter(jugadores -> !jugadores.equals(jugador)).collect(Collectors.toCollection(ArrayList::new));
    }

    public void addJugador(String jugador){
        if(!jugadores.contains(jugador)){
            jugadores.add(jugador);
        }
    }

    public int getVictorias(String jugador){
        return partidas.stream().filter(partida -> partida.getGanador().equals(jugador)).toList().size();
    }

    public List <String> rankingCampeones(){
        return jugadores.stream().sorted(Comparator.comparing(this::getVictorias)).toList().reversed();
    }



}
