package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Biblioteca;

import java.io.IOException;

@WebServlet("/biblioteca")
public class ConsultarBiblioteca extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false)==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe una sesion");
            return;
        }

        Biblioteca biblioteca = (Biblioteca) req.getSession().getAttribute("biblioteca");

        if (biblioteca==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No configuraste tu biblioteca");
            return;
        }

        resp.getWriter().println("Nombre: "+biblioteca.getNombre());
        resp.getWriter().println("Capacidad máxima: "+biblioteca.getCapacidad());
        resp.getWriter().println("Libros almacenados: "+biblioteca.getNumLibros());
    }
}
