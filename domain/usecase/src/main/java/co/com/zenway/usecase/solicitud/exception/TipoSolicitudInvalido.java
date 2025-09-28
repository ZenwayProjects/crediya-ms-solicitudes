package co.com.zenway.usecase.solicitud.exception;

public class TipoSolicitudInvalido extends RuntimeException{

    public TipoSolicitudInvalido(String mensaje){
        super(mensaje);
    }
}
