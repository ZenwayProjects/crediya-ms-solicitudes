package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.Usuario.Usuario;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

import java.util.List;

@RequiredArgsConstructor
public class AsesorSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioServiceRepository usuarioServiceRepository;





    public Flux<SolicitudesPendientesDto> buscarSolicitudesPendientes(
            List<String> estadosSolicitud,
            String tipoPrestamoNombre,
            int page,
            int size
    ) {
        int offset = page * size;

        return solicitudRepository.obtenerSolicitudesPendientes(estadosSolicitud, tipoPrestamoNombre, size, offset)
                .collectList()
                .flatMapMany(solicitudesPendientesList -> {
                    List<String> emails = solicitudesPendientesList.stream()
                            .map(SolicitudesPendientesDto::getEmail)
                            .filter(java.util.Objects::nonNull)
                            .distinct()
                            .toList();

                    return usuarioServiceRepository.buscarUsuariosPorEmail(emails)
                            .collectMap(Usuario::getEmail)
                            .flatMapMany(usuariosPorEmail ->
                                    Flux.fromIterable(solicitudesPendientesList)
                                            .map(dto -> {
                                                Usuario usuario = usuariosPorEmail.get(dto.getEmail());
                                                if (usuario != null) {
                                                    dto.setNombre(usuario.getNombreCompleto());
                                                    dto.setSalarioBase(usuario.getSalarioBase());
                                                }
                                                return dto;
                                            })
                            );
                });
    }



}

