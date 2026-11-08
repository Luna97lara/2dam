package org.example;


import config.ThymeleafConstants;
import config.UrlConstants;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Partida;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import java.io.IOException;

@WebServlet("/juego")
public class Juego extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Partida partida = (Partida) req.getSession().getAttribute("partida");
        String coordenada=req.getParameter("coordenada");
        String casillascompletadas = " ";

        if (partida==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "No existe una partida creada");
            return;
        }

        if (coordenada==null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Klk con la coordenada mi loco");
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
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Estás intentando pintar sobre un hueco ocupado");
            return;
        }

        if(partida.getTurno().equalsIgnoreCase(partida.getJugador1())){
            tablero[Integer.parseInt(coordenada) - 1] = partida.getMarca1();
            partida.setTurno(partida.getJugador2());
        } else {
            tablero[Integer.parseInt(coordenada) - 1] = partida.getMarca2();
            partida.setTurno(partida.getJugador1());
        }



        String fila1 = tablero[0] + " " + tablero[1] + " " + tablero[2];
        String fila2 = tablero[3] + " " + tablero[4] + " " + tablero[5];
        String fila3 = tablero[6] + " " + tablero[7] + " " + tablero[8];

        //Bucle para validar ganador en horizontales
        for (int i = 0; i <= 6; i = i + 3) {
            if (tablero[i].equals(tablero[i + 1]) && tablero[i].equals(tablero[i + 2])&&!tablero[i].equals("-")) {
                partida.setGanador("Gana " + tablero[i]);
            }
        }

        //Bucle para validar ganador vertical
        for (int i = 0; i < 3; i++) {
            if (tablero[i].equals(tablero[i + 3]) & tablero[i].equals(tablero[i + 6])&&!tablero[i].equals("-")) {
                partida.setGanador("Gana" + tablero[i]);
            }
        }

        //Condiciones para validar ganador en diagonales:
        if (tablero[0].equals(tablero[4]) && tablero[0].equals(tablero[8])&&!tablero[0].equals("-")) {
            partida.setGanador("Gana" + tablero[0]);
        }

        if (tablero[2].equals(tablero[4]) && tablero[2].equals(tablero[6])&&!tablero[2].equals("-")) {
            partida.setGanador("Gana" + tablero[0]);
        }

        boolean completado= true;
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
        ctx.setVariable("ganador", partida.getGanador());
        ctx.setVariable("casillascompletadas", casillascompletadas);
        resp.setContentType(ThymeleafConstants.CONTENT_TYPE);
        ((TemplateEngine) getServletContext().getAttribute(ThymeleafConstants.TEMPLATE_ENGINE_ATTR))
                .process(UrlConstants.TEMPLATE_TRES_EN_RAYA, ctx, resp.getWriter());



    }
}
