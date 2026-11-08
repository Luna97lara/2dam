package config;

/**
 * Constantes relacionadas con URLs y rutas de la aplicación web
 */

public final class UrlConstants {

        // Constructor privado para prevenir instanciación
        private UrlConstants() {
            throw new UnsupportedOperationException("Esta es una clase de utilidad");
        }

        // Rutas de plantillas Thymeleaf
        public static final String TEMPLATE_PREFIX = "/WEB-INF/templates/";
        public static final String TEMPLATE_SUFFIX = ".html";

        // Nombres de plantillas
        public static final String TEMPLATE_TRES_EN_RAYA = "tresEnRaya";
        public static final String TEMPLATE_CREAR_PARTIDA = "crearPartida";
        public static final String TEMPLATE_ESTADISTICAS = "estadisticas";
    }

