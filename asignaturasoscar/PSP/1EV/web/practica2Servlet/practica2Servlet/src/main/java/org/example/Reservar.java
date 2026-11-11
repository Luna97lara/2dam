package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cine;

import java.io.IOException;

@WebServlet("/reservar")
public class Reservar extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getSession(false) == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe una sesion, tolai");
            return;
        }

        Cine cine = (Cine) req.getSession().getAttribute("cine");

        if(cine == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No existe ese cine, esquizofrénico");
            return;
        }

        String cliente = req.getParameter("cliente");
        String titulo = req.getParameter("titulo");
        String entrada = req.getParameter("entrada");



    }
}
