package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Torneo;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/stats")
public class Estadisticas extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Torneo torneo = (Torneo) req.getSession().getAttribute("torneo");

        if (torneo == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }

        PrintWriter out = resp.getWriter();

        torneo.getPartidas().stream().forEach(p -> {
            out.println("-----Partida-----");

            if (p.getGanador() == null) {
                out.println("Ganador: empate entre " + p.getJugador1()
                        + " y " + p.getJugador2());
            } else {
                out.println("Ganador: " + p.getGanador());
                out.println("Perdedor: " + p.getTurno());
            }
        });
    }
    }
