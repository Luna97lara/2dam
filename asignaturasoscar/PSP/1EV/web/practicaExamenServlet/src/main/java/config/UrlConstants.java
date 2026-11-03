package config;

public final class UrlConstants {
    // Constructor privado para prevenir instanciación
    private UrlConstants() {
        throw new UnsupportedOperationException("Esta es una clase de utilidad");
    }

    // URLs de los servlets
    public static final String URL_PARESIMPARES = "/paresimpares";

    // Rutas de plantillas Thymeleaf
    public static final String TEMPLATE_PREFIX = "/WEB-INF/templates/";
    public static final String TEMPLATE_SUFFIX = ".html";

    // Nombres de plantillas
    public static final String TEMPLATE_NUMERITO = "numerito";

}
