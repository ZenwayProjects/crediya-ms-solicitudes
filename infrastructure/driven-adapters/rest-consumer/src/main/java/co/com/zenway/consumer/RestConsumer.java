package co.com.zenway.consumer;

import co.com.zenway.consumer.dto.UsuarioInfoSolicitudBoundaryDTO;
import co.com.zenway.model.solicitud.dto.UsuarioInfoSolicitudDTO;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class RestConsumer implements UsuarioServiceRepository/* implements Gateway from domain */{
    private final WebClient client;



    @Override
    @CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackEmail")
    public Mono<UsuarioInfoSolicitudDTO> obtenerUsuarioInfoPorDocumento(String documento) {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .flatMap(auth -> {
                    Object credentials = auth.getCredentials();
                    String token;
                    if(credentials instanceof Jwt jwt){
                        token = jwt.getTokenValue();
                    }else {
                        token = credentials.toString();
                    }
                    return client
                            .get()
                            .uri("/api/v1/usuarios/email/{documento}", documento)
                            .headers(header -> header.setBearerAuth(token))
                            .retrieve()
                            .bodyToMono(UsuarioInfoSolicitudBoundaryDTO.class)
                            .map(boundary -> new UsuarioInfoSolicitudDTO(
                                    boundary.id(),
                                    boundary.email()
                            ))
                            .doOnError(e -> log.info("Error al llamar el ms-auth: {} :{}", token, e.getMessage()));
                });
    }


    private Mono<UsuarioInfoSolicitudDTO> fallbackEmail(String documento, Throwable ex) {
        log.info("Error consultando usuarios: {}", ex.getMessage());
        return Mono.error(new RuntimeException("Servicio no disponible"));
    }
}
