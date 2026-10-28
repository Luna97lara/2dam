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

@WebServlet("/tres")
public class TresEnRaya extends HttpServlet {


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String marca = req.getParameter("marca");
        String coordenada = req.getParameter("coordenada");
        String casillascompletadas = " ";
        String ganador = " ";
        String jugador1="X";
        String jugador2="O";


        //Validación de que la marca solo es X/O:
        if(!(marca.equalsIgnoreCase("X") || marca.equalsIgnoreCase("O"))) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Marca inválida picha, jugamos con X y O");
        }

        //Comprobar que me manda los parámetros:
        if (marca == null || coordenada == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "A ver si te enteras cabrón, parametro requerido");
            return;
        }

        //Inicializar la partida en X
        if (req.getSession().getAttribute("marca") == null) {
            req.getSession().setAttribute("marca", jugador1);
        }


        //Respetar el turno
        if(!marca.equals(req.getSession().getAttribute("marca"))){
            resp.sendError(HttpServletResponse.SC_FORBIDDEN, "Eres un fullero picha, no te toca a ti");
            return;
        }

        // Pintar el tablerito
        String[] tablero = (String[]) req.getSession().getAttribute("Tablero");
        if (tablero == null) {
            tablero = new String[]{"-", "-", "-", "-", "-", "-", "-", "-", "-"};
            req.getSession().setAttribute("Tablero", tablero);
        }


        //Validar que no se sobreescribe sobre un hueco ocupado
        if(!tablero[Integer.parseInt(coordenada)-1].equals("-")){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Estás intentando pintar sobre un hueco ocupado");
        }

        tablero[Integer.parseInt(coordenada) - 1] = marca;

        String fila1 = tablero[0] + " " + tablero[1] + " " + tablero[2];
        String fila2 = tablero[3] + " " + tablero[4] + " " + tablero[5];
        String fila3 = tablero[6] + " " + tablero[7] + " " + tablero[8];


        //Cambios de turno (X/O)
        if(req.getSession().getAttribute("marca") == jugador1) {
            req.getSession().setAttribute("marca", jugador2);
        } else {
            req.getSession().setAttribute("marca", jugador1);
        }

        boolean completado= true;
        //Bucle para validar ganador en horizontales
        for (int i = 0; i <= 6; i = i + 3) {
            if (tablero[i].equals(tablero[i + 1]) && tablero[i].equals(tablero[i + 2])&&!tablero[i].equals("-")) {
                ganador = "Gana " + tablero[i];
            }
        }

        //Bucle para validar ganador vertical
        for (int i = 0; i < 3; i++) {
            if (tablero[i].equals(tablero[i + 3]) & tablero[i].equals(tablero[i + 6])&&!tablero[i].equals("-")) {
                ganador = "Gana" + tablero[i];
            }
        }

        //Condiciones para validar ganador en diagonales:
        if (tablero[0].equals(tablero[4]) && tablero[0].equals(tablero[8])&&!tablero[0].equals("-")) {
            ganador = "Gana " + tablero[0];
        }

        if (tablero[2].equals(tablero[4]) && tablero[2].equals(tablero[6])&&!tablero[2].equals("-")) {
            ganador = "Gana " + tablero[0];
        }

        for (int i = 0; i < 8; i++) {
            if (tablero[i].equals("-")) {
                completado = false;
            }
        }

        if (completado) {
            casillascompletadas="No quedan huecos para seguir jugando";
        }

        var webExchange = JakartaServletWebApplication.buildApplication(getServletContext())
                .buildExchange(req, resp);
        WebContext ctx = new WebContext(webExchange);
        ctx.setVariable("fila1", fila1);
        ctx.setVariable("fila2", fila2);
        ctx.setVariable("fila3", fila3);
        ctx.setVariable("ganador", ganador);
        ctx.setVariable("casillascompletadas", casillascompletadas);
        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_TRES_EN_RAYA, ctx, resp.getWriter());


    }
}
