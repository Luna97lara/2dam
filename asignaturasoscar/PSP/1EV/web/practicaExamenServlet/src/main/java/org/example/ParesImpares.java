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

@WebServlet("/paresimpares")
public class ParesImpares extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String numerointroducido = req.getParameter("numerointroducido");
        String tipo = " ";

        if (Integer.parseInt(numerointroducido) % 2 ==  0) {
            tipo= "Número par";
        } else {
            tipo = "Número impar";
        }

        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("tipo", tipo);
        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_NUMERITO, ctx, resp.getWriter());

    }
}
