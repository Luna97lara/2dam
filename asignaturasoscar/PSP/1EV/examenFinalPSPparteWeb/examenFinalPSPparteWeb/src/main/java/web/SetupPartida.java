package web;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Partida;
import model.Torneo;

import java.io.IOException;

@WebServlet("/setup")
public class SetupPartida extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String jugador1 = req.getParameter("jugador1");
        String jugador2 = req.getParameter("jugador2");
        String password = req.getParameter("nomecatees");

        if (jugador1 == null ||
                jugador2 == null ||
                password == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Faltan parámetros por introducir. Revísalo picha");
            return;
        }

        Torneo torneo;

        if(req.getSession().getAttribute("torneo")==null) {
            torneo= new Torneo();
            req.getSession().setAttribute("torneo", torneo);
        } else {
            torneo= (Torneo) req.getSession().getAttribute("torneo");
        }

        String palabraSecreta="Suspenso";

        torneo.addPartida(new Partida(jugador1,  jugador2, palabraSecreta));

        resp.getWriter().println("Partida creada." +
                "Jugadores: "+jugador1+ " y "+jugador2);
        torneo.addJugador(jugador1);
        torneo.addJugador(jugador2);

    }
}
