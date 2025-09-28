package co.com.zenway.api.utils;

public final class ConstantesErrores {

    private ConstantesErrores() {
        throw new IllegalStateException("Clase de constantes para errores");
    }

    public static final String BODY_REQUERIDO = "El cuerpo de la solicitud es obligatorio";
    public static final String ARGUMENTO_NO_VALIDO = "Argumento no valido";
    public static final String ERROR_INTERNO = "Error interno del servidor";
    public static final String VALIDACION_FALLIDA = "Validación fallida";
    public static final String SIN_PERMISOS_403 = "Acceso denegado: no tiene permisos";
    public static final String NO_AUTORIZADO_401 = "No autorizado: token no valido o ausente";

}
