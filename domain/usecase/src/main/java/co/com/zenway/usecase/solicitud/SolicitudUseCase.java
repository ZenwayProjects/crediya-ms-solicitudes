package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioServiceRepository usuarioServiceRepository;


    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud, String documentoIdentidad){
        return usuarioServiceRepository.obtenerEmailPorDocumento(documentoIdentidad)
                .flatMap(email -> {
                    solicitud.setEmail(email);
                    return solicitudRepository.enviarSolicitudDePrestamo(solicitud);
                });
    }



}
