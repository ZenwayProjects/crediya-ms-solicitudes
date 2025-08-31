package co.com.zenway.api;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class GlobalErrorHandler {
    private static final String MENSAJE = "mensaje";
    private static final String ERROR = "error";


    public Mono<ServerResponse> handler(Throwable exception){
        if(exception instanceof ConstraintViolationException violationException){
            return constraintViolationHandler(violationException);
        } else if (exception instanceof IllegalArgumentException ie) {
            return illegalArgumentHandler(ie);
        }
        else {
            return genericErrorHandler(exception);
        }
    }

    private Mono<ServerResponse> genericErrorHandler(Throwable e){
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(Map.of(
                        ERROR, "Error interno del servidor",
                        MENSAJE, e.getMessage() != null ? e.getMessage() : "Ocurrió un error inesperado"
                ));

    }

    private Mono<ServerResponse> constraintViolationHandler(ConstraintViolationException e){
        var constraintViolations =e.getConstraintViolations().stream()
                .map(c -> Map.of("campo", c.getPropertyPath().toString(),
                        MENSAJE, c.getMessage()))
                .toList();

        return ServerResponse.badRequest().bodyValue(Map.of(
                ERROR, "Validacion fallida",
                "violacion", constraintViolations));
    }

    private Mono<ServerResponse> illegalArgumentHandler(IllegalArgumentException illegalArgumentException){
        return ServerResponse.badRequest().bodyValue(Map.of(
                ERROR, "Argumento no válido",
                MENSAJE,illegalArgumentException.getMessage()
        ));

    }
}
