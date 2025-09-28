package co.com.zenway.model.tipoprestamo.gateways;

import co.com.zenway.model.tipoprestamo.TipoPrestamo;
import reactor.core.publisher.Mono;

public interface TipoPrestamoRepository {
    Mono<Boolean> existsById(Short id);
    Mono<TipoPrestamo> buscarPorId(Short id);
}
