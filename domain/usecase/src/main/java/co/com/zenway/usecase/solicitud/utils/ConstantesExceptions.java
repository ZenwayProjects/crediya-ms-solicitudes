package co.com.zenway.usecase.solicitud.utils;

public final class ConstantesExceptions {
    private ConstantesExceptions() {
        throw new IllegalArgumentException("Clase de utils de excepciones");
    }

    public static final String TIPO_SOLICITUD_INVALIDO = "El tipo de solicitud no es valido";
    public static final String MONTO_DEL_PRESTAMO_INVALIDO = "El monto para este tipo de prestamo no es valido";

}
