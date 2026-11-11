package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Torneo;

import java.io.IOException;

@WebServlet ("/fin")
public class FinTorneo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Torneo torneo = (Torneo) req.getSession().getAttribute("torneo");

        if (torneo==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }

        Integer llamada = (Integer) req.getSession().getAttribute("llamada");

        if (llamada==null){
            llamada=0;
        }

        switch (llamada){
            case 0:
                resp.getWriter().println("Tercer puesto: "+torneo.rankingCampeones().get(2));
                llamada ++;
                req.getSession().setAttribute("llamada",llamada);
                break;
            case 1:
                resp.getWriter().println("Segundo puesto: "+torneo.rankingCampeones().get(1));
                llamada ++;
                req.getSession().setAttribute("llamada",llamada);
                break;
            case 2:
                resp.getWriter().println("Primer puesto: "+torneo.rankingCampeones().getFirst());
                break;
        }






    }
}
