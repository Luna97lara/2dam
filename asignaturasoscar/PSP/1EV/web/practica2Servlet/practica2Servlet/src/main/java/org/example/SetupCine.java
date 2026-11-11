package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Cine;

import java.io.IOException;

@WebServlet("/setup")
public class SetupCine extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String nombreCine = req.getParameter("cine");
        if(nombreCine == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Pon el nombre del cine chulo");
            return;
        }

        Cine cine = new Cine(nombreCine);

        req.getSession().setAttribute("cine", cine);

    }
}
