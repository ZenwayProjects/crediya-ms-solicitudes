package co.com.zenway.sqs.sender.solicitud;

import co.com.zenway.model.solicitud.dto.SolicitudParaLambdaDto;
import co.com.zenway.model.sqs.dto.MensajeCambioEstadoSolicitudSQSDto;
import co.com.zenway.model.sqs.dto.SolicitudAprobadaEvent;
import co.com.zenway.model.sqs.dto.SolicitudAutoValidacionSQSDto;
import co.com.zenway.model.sqs.gateways.EventosSQSRepository;
import co.com.zenway.sqs.sender.SQSSender;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

import static co.com.zenway.sqs.sender.solicitud.constantes.SolicitudNombreCola.*;

@Service
@Log4j2
@RequiredArgsConstructor
public class EventosSQSAdapter implements EventosSQSRepository {

    private final SQSSender sender;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Mono<MensajeCambioEstadoSolicitudSQSDto> enviarNotificacionDeEstadoCredito(MensajeCambioEstadoSolicitudSQSDto mensajeCambioEstadoSolicitudSQSDto) {
        return Mono.fromCallable(() -> toJson(mensajeCambioEstadoSolicitudSQSDto))
                .flatMap(json -> sender.send(COLA_NOTIFICACION, json))
                .thenReturn(mensajeCambioEstadoSolicitudSQSDto);
    }

    @Override
    public Mono<Void> enviarSolicitudDePrestamoConAutoValidacion(SolicitudParaLambdaDto solicitudParaLambdaDto, List<SolicitudParaLambdaDto> solicitudesAprobadasListDto) {
        SolicitudAutoValidacionSQSDto payload = new SolicitudAutoValidacionSQSDto(solicitudParaLambdaDto, solicitudesAprobadasListDto);

        return Mono.fromCallable(() -> {
                    String json = toJson(payload);
                    log.info("Enviando a cola [{}]: {}", COLA_REGISTRO_SOLICITUD, json);
                    return json;
                })
                .flatMap(json -> sender.send(COLA_REGISTRO_SOLICITUD, json))
                .then();
    }

    @Override
    public Mono<Void> enviarEventoSolicitudAprobada(SolicitudAprobadaEvent evento) {
        return Mono.fromCallable(() -> {
                    try {
                        String json = objectMapper.writeValueAsString(evento);
                        log.info("Enviando solicitud aprobada a cola [{}]: {}", COLA_SOLICITUD_APROBADA, json);
                        return json;
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException("Error serializando evento solicitud aprobada", e);
                    }
                })
                .flatMap(json -> sender.send(COLA_SOLICITUD_APROBADA, json))
                .doOnSuccess(v -> log.info("Evento de solicitud aprobada enviado correctamente: {}", evento.getIdSolicitud()))
                .doOnError(e -> log.error("Error enviando evento de solicitud aprobada", e)).then();
    }


    private String toJson(Object dto) {
        try {
            return objectMapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando mensaje SQS", e);
        }
    }

}
