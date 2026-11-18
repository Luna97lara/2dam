package web;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Partida;
import model.Torneo;

import java.io.IOException;

@WebServlet("/ahorcado")
public class Ahorcado extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Torneo torneo= (Torneo) req.getSession().getAttribute("torneo");
        if(torneo==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe un torneo creado");
            return;
        }

        Partida partida = torneo.getPartidas().getLast();

        if (partida==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }

        String letra = req.getParameter("letra");

        if (letra==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Escribe una letra hijo");
            return;
        }




    }
}
