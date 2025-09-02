package co.com.zenway.usecase.solicitud.exception;

public class MontoNoValido extends RuntimeException{
    public MontoNoValido(String mensaje){
        super(mensaje);
    }
}
