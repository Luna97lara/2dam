package org.example;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.CajaFuerte;

import java.io.IOException;

@WebServlet("/config")
public class ConfigurarCajaFuerte extends HttpServlet {
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String numero1 = request.getParameter("numero1");
        String numero2 = request.getParameter("numero2");
        String numero3 = request.getParameter("numero3");

        if (numero1 == null || numero2 == null || numero3 == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debes introducir los tres dígitos indicados");
            return;
        }

        int n1=0;
        int n2=0;
        int n3=0;

        try {
            n1 = Integer.parseInt(numero1);
            n2 = Integer.parseInt(numero2);
            n3 = Integer.parseInt(numero3);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Debes introducir números");
        }

        if (n1<0 || n1>9
        || n2<0 || n2>9
        || n3<0 || n3>9) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST,  "Debes introducir números de un solo dígito");
            return;
        }

        CajaFuerte cajaFuerte = new CajaFuerte(numero1, numero2, numero3);

        request.getSession().setAttribute("cajaFuerte", cajaFuerte);

    }
}
