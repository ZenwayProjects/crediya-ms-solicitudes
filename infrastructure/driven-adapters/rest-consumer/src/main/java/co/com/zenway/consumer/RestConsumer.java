package co.com.zenway.consumer;

import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Log4j2
public class RestConsumer implements UsuarioServiceRepository/* implements Gateway from domain */{
    private final WebClient client;


    // these methods are an example that illustrates the implementation of WebClient.
    // You should use the methods that you implement from the Gateway from the domain.
    @CircuitBreaker(name = "testGet" /*, fallbackMethod = "testGetOk"*/)
    public Mono<ObjectResponse> testGet() {
        return client
                .get()
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }

// Possible fallback method
//    public Mono<String> testGetOk(Exception ignored) {
//        return client
//                .get() // TODO: change for another endpoint or destination
//                .retrieve()
//                .bodyToMono(String.class);
//    }

    @CircuitBreaker(name = "testPost")
    public Mono<ObjectResponse> testPost() {
        ObjectRequest request = ObjectRequest.builder()
            .val1("exampleval1")
            .val2("exampleval2")
            .build();
        return client
                .post()
                .body(Mono.just(request), ObjectRequest.class)
                .retrieve()
                .bodyToMono(ObjectResponse.class);
    }

    @Override
    @CircuitBreaker(name = "usuarioService", fallbackMethod = "fallbackEmail")
    public Mono<String> obtenerEmailPorDocumento(String documento) {
        return client
                .get()
                .uri("/api/v1/usuarios/email/{documento}", documento)
                .retrieve()
                .bodyToMono(String.class);
    }

    private Mono<String> fallbackEmail(String documento, Throwable ex) {
        log.info("Error consultando usuarios: {}", ex.getMessage());
        return Mono.error(new RuntimeException("Servicio no disponible"));
    }
}
