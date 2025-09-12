package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.Usuario.Usuario;
import co.com.zenway.model.solicitud.dto.DeudaTotalAprobadaPorUsuarioDto;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

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
                    if (emails.isEmpty()) {
                        return Flux.fromIterable(solicitudesPendientesList);
                    }

                    Mono<Map<String, Usuario>> usuarios =usuarioServiceRepository.buscarUsuariosPorEmail(emails)
                            .collectMap(Usuario::getEmail);

                    Mono<Map<String, BigDecimal>> deudas =
                            solicitudRepository.obtenerSumaDeudaTotalPorEmails(emails)
                                    .collectMap(DeudaTotalAprobadaPorUsuarioDto::getEmail, DeudaTotalAprobadaPorUsuarioDto::getDeudaTotalAprobada);


                    return Mono.zip(usuarios, deudas)
                            .flatMapMany(tupla -> {
                                Map<String, Usuario> usuariosPorEmail = tupla.getT1();
                                Map<String, BigDecimal> deudasPorEmail = tupla.getT2();

                                return Flux.fromIterable(solicitudesPendientesList)
                                        .map(dto-> {
                                            Usuario usuario = usuariosPorEmail.get(dto.getEmail());
                                            if(usuario != null){
                                                dto.setNombre(usuario.getNombreCompleto());
                                                dto.setSalarioBase(usuario.getSalarioBase());
                                            }
                                            BigDecimal deuda = deudasPorEmail.getOrDefault(dto.getEmail(), BigDecimal.ZERO);
                                            dto.setDeudaTotalAprobada(deuda);
                                            return dto;
                                        });
                            });

                });
    }



}

