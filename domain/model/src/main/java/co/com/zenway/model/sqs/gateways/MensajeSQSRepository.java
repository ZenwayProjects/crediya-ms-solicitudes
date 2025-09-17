package co.com.zenway.model.sqs.gateways;

import co.com.zenway.model.sqs.MensajeSQSDto;
import reactor.core.publisher.Mono;

public interface MensajeSQSRepository {

    Mono<MensajeSQSDto> enviarNotificacionDeEstadoCredito(MensajeSQSDto mensajeSQSDto);
}
