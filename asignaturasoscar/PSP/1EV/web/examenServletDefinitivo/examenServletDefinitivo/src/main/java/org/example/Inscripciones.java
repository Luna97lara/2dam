package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Carrera;

import java.io.IOException;

@WebServlet("/inscripciones")
public class Inscripciones extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe una sesión");
            return;
        }

        String nombre = req.getParameter("nombre");

        if (nombre == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro requerido, introduce un nombre para participar");
            return;
        }

        nombre = nombre.trim();

        if(nombre.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El nombre que has introducido está vacío");
            return;
        }

        Carrera carrera = new Carrera();

        carrera.addParticipante(nombre);

        req.getSession().setAttribute("carrera", carrera);

        resp.getWriter().println("Inscrito: " + nombre);



    }
}
