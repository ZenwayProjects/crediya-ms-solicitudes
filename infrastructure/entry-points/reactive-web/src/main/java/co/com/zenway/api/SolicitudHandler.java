package co.com.zenway.api;

import co.com.zenway.api.dto.SolicitudRegistroDTO;
import co.com.zenway.api.mapper.SolicitudMapper;
import co.com.zenway.usecase.solicitud.SolicitudUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Log4j2
public class SolicitudHandler {
private  final SolicitudUseCase solicitudUseCase;
private  final SolicitudMapper solicitudMapper;
private  final GlobalErrorHandler globalErrorHandler;


    public Mono<ServerResponse> solicitarCredito(ServerRequest serverRequest) {

        return serverRequest
                .bodyToMono(SolicitudRegistroDTO.class)
                .doOnSubscribe(info -> log.info("Iniciando solicitud"))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Body requerido")))
                .flatMap(dto -> {
                    var solicitud = solicitudMapper.toModel(dto);
                    return solicitudUseCase.registrarSolicitud(solicitud, dto.getDocumentoIdentidad());
                })
                .flatMap(solicitudGuardada -> ServerResponse
                        .status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(solicitudMapper.toResponse(solicitudGuardada)))
                .onErrorResume(globalErrorHandler::handler);
    }
}
