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

        if (torneo==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }
    }
}
