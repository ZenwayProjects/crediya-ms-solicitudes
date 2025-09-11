package co.com.zenway.model.solicitud.gateways;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudRepository {

    Mono<Solicitud> enviarSolicitudDePrestamo(Solicitud solicitud);

    Flux<SolicitudesPendientesDto> obtenerSolicitudesPendientes(List<String> estadoSolicitud, String tipoPrestamoNombre, int limit, int offset);




}
