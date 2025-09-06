package co.com.zenway.model.solicitud.gateways;

import co.com.zenway.model.solicitud.dto.UsuarioInfoSolicitudDTO;
import reactor.core.publisher.Mono;

public interface UsuarioServiceRepository {
    Mono<UsuarioInfoSolicitudDTO> obtenerUsuarioInfoPorDocumento(String documento);
}
