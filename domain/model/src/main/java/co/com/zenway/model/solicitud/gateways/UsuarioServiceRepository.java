package co.com.zenway.model.solicitud.gateways;

import co.com.zenway.model.Usuario.Usuario;
import co.com.zenway.model.solicitud.dto.UsuarioInfoSolicitudDTO;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface UsuarioServiceRepository {
    Mono<UsuarioInfoSolicitudDTO> obtenerUsuarioInfoPorDocumento(String documento);

    Flux<Usuario> buscarUsuariosPorEmail(List<String> email);

}
