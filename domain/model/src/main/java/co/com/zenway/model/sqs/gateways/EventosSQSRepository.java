package co.com.zenway.model.sqs.gateways;

import co.com.zenway.model.solicitud.dto.SolicitudParaLambdaDto;
import co.com.zenway.model.sqs.dto.MensajeCambioEstadoSolicitudSQSDto;
import reactor.core.publisher.Mono;

import java.util.List;

public interface EventosSQSRepository {

    Mono<MensajeCambioEstadoSolicitudSQSDto> enviarNotificacionDeEstadoCredito(MensajeCambioEstadoSolicitudSQSDto mensajeCambioEstadoSolicitudSQSDto);

    Mono<Void> enviarSolicitudDePrestamoConAutoValidacion(SolicitudParaLambdaDto solicitudParaLambdaDto, List<SolicitudParaLambdaDto> solicitudesAprobadasListDto);


}
