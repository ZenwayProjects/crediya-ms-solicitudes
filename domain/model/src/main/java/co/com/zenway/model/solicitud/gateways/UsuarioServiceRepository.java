package co.com.zenway.model.solicitud.gateways;

import reactor.core.publisher.Mono;

public interface UsuarioServiceRepository {
    Mono<String> obtenerEmailPorDocumento(String documento);
}
