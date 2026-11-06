package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CajaFuerte;

import java.io.IOException;

@WebServlet("/abrir")
public class AbrirCajaFuerte extends HttpServlet {
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

        //Aquí miramos si ya está abierta de antes
        if(cajaFuerte.isAbierta()){
            resp.getWriter().println("Tu caja ya está abierta picha");
            return;
        }

        String numeroIntroducido = req.getParameter("numero");

        if (numeroIntroducido == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El número introducido no puede ser nulo");
            return;
        }

        try {
            int numero = Integer.parseInt(numeroIntroducido);
            if(numero<0 || numero>9){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El número tiene que ser de un dígito");
                return;
            }
            switch (cajaFuerte.getProgreso()) {
            case 0:
                if (numero == Integer.parseInt(cajaFuerte.getNumero1())) {
                    cajaFuerte.setProgreso(cajaFuerte.getProgreso() + 1);
                } else {
                    cajaFuerte.setProgreso(0);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Te has equivocao tonto, empieza de nuevo.");
                    return;
                }
                break;
            case 1:
                if (numero == Integer.parseInt(cajaFuerte.getNumero2())) {
                    cajaFuerte.setProgreso(cajaFuerte.getProgreso() + 1);
                } else {
                    cajaFuerte.setProgreso(0);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Te has equivocao tonto, empieza de nuevo.");
                    return;
                }
                break;
            case 2:
                if (numero == Integer.parseInt(cajaFuerte.getNumero3())) {
                    cajaFuerte.setProgreso(cajaFuerte.getProgreso() + 1);
                } else {
                    cajaFuerte.setProgreso(0);
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                            "Te has equivocao tonto, empieza de nuevo.");
                    return;
                }
                break;
        }
        } catch (NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "El código tiene que tratarse de dígitos");
            return;
        }
        cajaFuerte.abrir();


        //Aquí le decimos al pringaillo que la ha conseguido abrir por fin
        if(cajaFuerte.isAbierta()){
            resp.getWriter().println("La abriste!!");
        }


    }
}
