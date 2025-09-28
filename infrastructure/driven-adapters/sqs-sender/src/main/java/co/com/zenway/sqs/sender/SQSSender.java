package co.com.zenway.sqs.sender;

import co.com.zenway.sqs.sender.config.SQSSenderProperties;
import co.com.zenway.sqs.sender.solicitud.constantes.SolicitudNombreCola;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender  {
    private final SQSSenderProperties properties;
    private final SqsAsyncClient client;

    public Mono<String> send(SolicitudNombreCola cola, String message) {
        return Mono.fromCallable(() -> buildRequest(cola, message))
                .flatMap(request -> Mono.fromFuture(client.sendMessage(request)))
                .doOnNext(response -> log.info("Mensaje enviado a {} con id {}", cola, response.messageId()))
                .map(SendMessageResponse::messageId);
    }


    private SendMessageRequest buildRequest(SolicitudNombreCola cola, String message) {
        String queueUrl = properties.queues().get(cola.getKey());
        if (queueUrl == null) {
            throw new IllegalArgumentException("Cola no configurada: " + cola);
        }

        return SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();
    }



}
