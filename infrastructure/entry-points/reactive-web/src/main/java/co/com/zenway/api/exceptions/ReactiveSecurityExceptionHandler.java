package co.com.zenway.api.exceptions;


import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static co.com.zenway.api.utils.ConstantesErrores.NO_AUTORIZADO_401;
import static co.com.zenway.api.utils.ConstantesErrores.SIN_PERMISOS_403;

@Component
public class ReactiveSecurityExceptionHandler implements ServerAuthenticationEntryPoint, ServerAccessDeniedHandler {


    @Override
    public Mono<Void> commence(ServerWebExchange exchange, AuthenticationException ex) {
        return writeErrorResponse(exchange, HttpStatus.UNAUTHORIZED, NO_AUTORIZADO_401);
    }

    @Override
    public Mono<Void> handle(ServerWebExchange exchange,
                             org.springframework.security.access.AccessDeniedException ex) {
        return writeErrorResponse(exchange, HttpStatus.FORBIDDEN, SIN_PERMISOS_403);
    }

    private Mono<Void> writeErrorResponse(ServerWebExchange exchange,
                                          HttpStatus status,
                                          String message) {
        exchange.getResponse().setStatusCode(status);
        exchange.getResponse().getHeaders().setContentType(MediaType.APPLICATION_JSON);

        String body = String.format("{\"status\": %d, \"error\": \"%s\"}",
                status.value(), message);

        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        return exchange.getResponse()
                .writeWith(Mono.just(exchange.getResponse()
                        .bufferFactory().wrap(bytes)))
                .then();
    }
}
