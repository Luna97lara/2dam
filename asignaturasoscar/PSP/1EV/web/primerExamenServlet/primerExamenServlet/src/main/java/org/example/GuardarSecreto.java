package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CajaFuerte;

import java.io.IOException;

@WebServlet("/guardar")
public class GuardarSecreto extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (req.getSession(false) == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe una sesión :O");
            return;
        }

        CajaFuerte cajaFuerte = (CajaFuerte) req.getSession(false).getAttribute("cajaFuerte");

        if (cajaFuerte == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No has configurado tu caja fuerte :/");
            return;
        }

        if(!cajaFuerte.isAbierta()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No has abierto la caja fuerte hijo");
            return;
        }

        String nombre = req.getParameter("nombre");
        String valor = req.getParameter("valor");

        if(nombre == null || valor == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro nulo. Introduce nombre y valor porfis");
            return;
        }

        nombre = nombre.trim();
        valor = valor.trim();

        if(nombre.isEmpty() || valor.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro vacío. Introduce nombre y valor porfis");
            return;
        }

        cajaFuerte.guardarSecretillo(nombre,valor);

        resp.getWriter().println("Secreto guardado exitosamente");



    }
}
