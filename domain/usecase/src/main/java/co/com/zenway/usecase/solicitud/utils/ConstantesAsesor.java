package co.com.zenway.usecase.solicitud.utils;

public final class ConstantesAsesor {

    private ConstantesAsesor(){
        throw new AssertionError("No se debe instanciar esta clase");
    }

    public static final Short ESTADO_SOLICITUD_APROBADO = 2;
    public static final Short ESTADO_SOLICITUD_RECHAZADO= 3;

    public static final String CORREO_POR_DEFECTO = "zenway15@gmail.com";
}
