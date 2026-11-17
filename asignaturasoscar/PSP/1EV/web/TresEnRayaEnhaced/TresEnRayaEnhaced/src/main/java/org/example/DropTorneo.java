package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Torneo;

import java.io.IOException;

@WebServlet("/drop")
public class DropTorneo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Torneo torneo = (Torneo) req.getSession().getAttribute("torneo");

        if (torneo == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }

        String jugador = req.getParameter("jugador");

        if (jugador == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Introduce el jugador porfa");
            return;
        }


        boolean existeJugador;
//        for (int i = 0; i < torneo.getPartidas().size(); i++) {
//            if (torneo.getPartidas().get(i).getJugador1().equals(jugador) || torneo.getPartidas().get(i).getJugador2().equals(jugador)) {
//                existeJugador = true;
//            }
//        }

        existeJugador = torneo.getPartidas().stream()
                .anyMatch(partida ->
                        partida.getJugador1().equals(jugador)
                                || partida.getJugador2().equals(jugador)
                );

        if (!existeJugador) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe un jugador con ese nombre");
        }

        torneo.dropTorneo(jugador);

        resp.getWriter().println("Jugador " + jugador + " borrado con éxito");

    }
}
