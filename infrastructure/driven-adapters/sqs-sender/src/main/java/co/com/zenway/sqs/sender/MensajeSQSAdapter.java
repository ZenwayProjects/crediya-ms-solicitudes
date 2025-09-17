package co.com.zenway.sqs.sender;

import co.com.zenway.model.sqs.MensajeSQSDto;
import co.com.zenway.model.sqs.gateways.MensajeSQSRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Log4j2
@RequiredArgsConstructor
public class MensajeSQSAdapter implements MensajeSQSRepository {

    private final SQSSender sender;

    @Override
    public Mono<MensajeSQSDto> enviarNotificacionDeEstadoCredito(MensajeSQSDto mensajeSQSDto) {
        String body = toJson(mensajeSQSDto);
        return sender.send(body).thenReturn(mensajeSQSDto);
    }

    private String toJson(MensajeSQSDto dto) {
        try {
            return new ObjectMapper().writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error serializando mensaje SQS", e);
        }
    }


}
