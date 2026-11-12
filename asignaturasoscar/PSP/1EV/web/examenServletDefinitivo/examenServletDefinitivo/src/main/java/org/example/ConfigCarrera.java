package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Carrera;

import java.io.IOException;

@WebServlet("/config")
public class ConfigCarrera extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String longitud = req.getParameter("longitud");

        if (longitud==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Introduce el parámetro, por favor.");
            return;
        }

        if(Integer.parseInt(longitud) == 0){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "La longitud no es correcta. Prueba con un número entero positivo");
            return;
        }

        req.getSession().setAttribute("longitud", longitud);
    }
}
