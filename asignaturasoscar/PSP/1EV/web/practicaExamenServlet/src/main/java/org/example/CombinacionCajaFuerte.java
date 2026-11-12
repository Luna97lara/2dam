package org.example;


import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;


@WebServlet("/combinacion")
public class CombinacionCajaFuerte extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String numero1 = req.getParameter("numero1");
            String numero2 = req.getParameter("numero2");
            String numero3 = req.getParameter("numero3");

            if (Integer.parseInt(numero1) >9 && Integer.parseInt(numero1)< 0
                    || Integer.parseInt(numero2) >9 && Integer.parseInt(numero2) < 0
                    || Integer.parseInt(numero3) >9 && Integer.parseInt(numero3) < 0) {
                resp
                        .sendError(HttpServletResponse.SC_BAD_REQUEST,
                        "Combinación inválida. Tienen que ser tres números del 1 al 9 cada uno");
            }


            req.setAttribute("numero1", numero1);

            req.setAttribute("numero2", numero2);

            req.setAttribute("numero3", numero3);



        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("numero1", numero1);
        ctx.setVariable("numero2", numero2);
        ctx.setVariable("numero3", numero3);
        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_CAJA_FUERTE, ctx, resp.getWriter());
    }
}
