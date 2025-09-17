package co.com.zenway.model.solicitud.gateways;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.DeudaTotalAprobadaPorUsuarioDto;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface SolicitudRepository {

    Mono<Solicitud> enviarSolicitudDePrestamo(Solicitud solicitud);

    Flux<SolicitudesPendientesDto> obtenerSolicitudesPendientes(List<String> estadoSolicitud, int estadosContador, String tipoPrestamoNombre, int limit, int offset);

    Flux<DeudaTotalAprobadaPorUsuarioDto> obtenerSumaDeudaTotalPorEmails(List<String> emails);

    Mono<Solicitud> obtenerSolicitudPorId(Long solicitudId);

    Mono<Long> actualizarEstadoSolicitud(Long solicitudId, Short nuevoEstado);




}
