package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Biblioteca;

import java.io.IOException;

@WebServlet("/setup")
public class SetupBiblioteca extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombre = req.getParameter("nombre");
        String capacidad = req.getParameter("capacidad");

        if (nombre == null
                || capacidad == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Faltan parámetros por introducir");
            return;
        }

        nombre = nombre.trim();

        if(nombre.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro vacío. Por favor, introduce un nombre");
            return;
        }

        int numeroLibros=0;

        try{
            numeroLibros = Integer.parseInt(capacidad);
        } catch(NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Capacidad invalida. Introduce solo dígitos");
            return;
        }

        if(numeroLibros <= 0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El número ha de ser positivo");
            return;
        }

        Biblioteca biblioteca = new Biblioteca(nombre, numeroLibros);

        req.getSession().setAttribute("biblioteca", biblioteca);

    }
}
