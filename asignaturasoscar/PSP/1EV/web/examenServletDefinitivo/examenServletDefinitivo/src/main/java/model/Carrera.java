package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Carrera {
    private List<String> participantes;

    public Carrera() {
        participantes = new ArrayList<>();
    }

    public List<String> getParticipantes() {
        return participantes;
    }

    public void addParticipante(String participante) {
        if (!participantes.contains(participante)) {
            participantes.add(participante);
        }

    }

}
