package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Biblioteca;
import model.Libro;

import java.io.IOException;

@WebServlet("/add")
public class AddLibro extends HttpServlet {
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
        String autor = req.getParameter("autor");

        if (titulo==null
                || autor==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros por introducir");
            return;
        }

        titulo = titulo.trim();
        autor = autor.trim();

        if (titulo.isEmpty() || autor.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Alguno de los parámetros está vacío");
            return;
        }

        Libro libro = new Libro(titulo,autor);

        if(biblioteca.getNumLibros()< biblioteca.getCapacidad()){
            biblioteca.addLibro(libro);
            resp.getWriter().println("Libro añadido correctamente");
        } else {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Capacidad completa. No se pueden añadir más libros");
            return;
        }

    }
}
