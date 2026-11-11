package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Biblioteca;
import model.Libro;

import java.io.IOException;

@WebServlet("/libro")
public class ConsultarLibro extends HttpServlet {
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

        String titulo = req.getParameter("titulo");

        if (titulo==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No introdujiste el parámetro");
            return;
        }

        titulo = titulo.trim();

        if (titulo.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El título del libro está vacío");
            return;
        }

        Libro libro = biblioteca.buscarLibro(titulo);

        if (libro==null){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Libro no encontrado");
            return;
        }

        resp.getWriter().println("Título: "+libro.getTitulo());
        resp.getWriter().println("Autor: "+libro.getAutor());

    }
}
