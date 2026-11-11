package org.example;

import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javassist.tools.web.BadHttpRequest;
import model.Partida;
import model.Torneo;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;
import java.util.List;

@WebServlet("/stats")
public class Estadisticas extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Torneo torneo = (Torneo) request.getSession().getAttribute("torneo");

        if (torneo == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }

        String jugador = request.getParameter("jugador");

        String partida = request.getParameter("partida");



        if (partida!=null){
            try {

                int numeroPartida = Integer.parseInt(partida);
                if (numeroPartida >= 0 && numeroPartida < torneo.getPartidas().size()) {
                    Partida p = torneo.getPartidas().get(numeroPartida);
                    for (String movimiento : p.getHistorial()) {
                        response.getWriter().println(movimiento);
                    }
                    return;
                } else {
                    response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "No existe una partida creada");
                    return;
                }
            } catch (NumberFormatException e) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Debe ingresar un numero de partida. El formato son dígitos");
                return;
            }
        }

        if (jugador == null) {
            for (int i = 0; i < torneo.getPartidas().size(); i++) {
                response.getWriter().println("-----Partida " + i + "-----");
                if (torneo.getPartidas().get(i).getGanador() == null) {
                    response.getWriter().println("Ganador: empate entre " + torneo.getPartidas().get(i).getJugador1() + " y " + torneo.getPartidas().get(i).getJugador2());
                } else {
                    response.getWriter().println("Ganador: " + torneo.getPartidas().get(i).getGanador());
                    response.getWriter().println("Perdedor: " + torneo.getPartidas().get(i).getTurno());
                }
                response.getWriter().println("Nº de movimientos: " + torneo.getPartidas().get(i).getMovimientos());
                response.getWriter().println("Marca de " + torneo.getPartidas().get(i).getJugador1() + ": " + torneo.getPartidas().get(i).getMarca1());
                response.getWriter().println("Marca de " + torneo.getPartidas().get(i).getJugador2() + ": " + torneo.getPartidas().get(i).getMarca2());
                response.getWriter().println("\n");
            }
        } else {
            boolean existeJugador = false;
            List<Partida> partidas = torneo.getPartidasByPlayer(jugador);
            if (partidas.size() > 0) {
                existeJugador = true;
                for (int i = 0; i < partidas.size(); i++) {
                    response.getWriter().println("-----Partida " + i + "-----");
                    if (partidas.get(i).getGanador() == null) {
                        response.getWriter().println("Empate entre " + partidas.get(i).getJugador1() + " y " + partidas.get(i).getJugador2());
                    } else {
                        response.getWriter().println("Victoria de " + partidas.get(i).getGanador() + " contra " + partidas.get(i).getTurno());
                    }
                    if (partidas.get(i).getJugador1().equals(jugador)) {
                        response.getWriter().println("Símbolo: " + partidas.get(i).getMarca1());
                    } else {
                        response.getWriter().println("Símbolo: " + partidas.get(i).getMarca2());
                    }
                    response.getWriter().println("Nº de movimientos: " + partidas.get(i).getMovimientos());
                    response.getWriter().println("\n");
                }
            }
            if (!existeJugador) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "No existen datos de este jugador");
            }
        }
    }

}
