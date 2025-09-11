package co.com.zenway.model.solicitud.gateways;

import co.com.zenway.model.solicitud.Solicitud;
import reactor.core.publisher.Mono;

public interface SolicitudRepository {

    Mono<Solicitud> enviarSolicitudDePrestamo(Solicitud solicitud);






}
