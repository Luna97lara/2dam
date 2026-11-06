package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CajaFuerte;

import java.io.IOException;

@WebServlet ("/consultar")
public class ConsultarSecreto extends HttpServlet {
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

        if (nombre == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro nulo. Tienes que introducir el nombre del secreto");
            return;
        }

        nombre = nombre.trim();

        if(nombre.isEmpty()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parámetro vacío. Tienes que introducir el nombre del secreto");
            return;
        }

        String secreto = cajaFuerte.consultarSecretillo(nombre);

        if(secreto == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Secreto no encontrado");
            return;
        }

        resp.getWriter().println(secreto);

    }
}
