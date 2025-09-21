package co.com.zenway.sqs.listener;

import co.com.zenway.model.sqs.dto.DecisionSolicitudDto;
import co.com.zenway.usecase.solicitud.AsesorSolicitudUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

import static co.com.zenway.usecase.solicitud.utils.ConstantesAsesor.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final AsesorSolicitudUseCase asesorSolicitudUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        try {
            // 1. Deserializar el mensaje
            var dto = new ObjectMapper()
                    .readValue(message.body(), DecisionSolicitudDto.class);

            // 2. Mapear la decisión a un estado
            Short nuevoEstadoId = mapDecisionToEstado(dto.getDecision());
            log.info("Mensaje recibido de SQS: {}", message.body());
            log.info("DTO parseado: {}", dto);

            // 3. Actualizar estado usando tu caso de uso existente
            return asesorSolicitudUseCase.actualizarEstadoSolicitud(dto.getIdSolicitud(), nuevoEstadoId)
                    .then();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }

    private Short mapDecisionToEstado(String decision) {
        return switch (decision) {
            case "APROBADO" -> ESTADO_SOLICITUD_APROBADO;
            case "RECHAZADO" -> ESTADO_SOLICITUD_RECHAZADO;
            default -> ESTADO_SOLICITUD_EN_REVISION;
        };
    }
}
