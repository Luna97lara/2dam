package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Carrera;

import java.io.IOException;

@WebServlet("/turnos")
public class Turnos extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe una sesión");
            return;
        }

        Carrera carrera = (Carrera) req.getSession().getAttribute("carrera");

        if (carrera == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe una carrera");
        }



    }
}
