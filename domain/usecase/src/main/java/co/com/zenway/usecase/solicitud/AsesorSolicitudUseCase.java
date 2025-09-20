package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.sqs.dto.MensajeCambioEstadoSolicitudSQSDto;
import co.com.zenway.model.sqs.gateways.EventosSQSRepository;
import co.com.zenway.model.Usuario.Usuario;
import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.DeudaTotalAprobadaPorUsuarioDto;
import co.com.zenway.model.solicitud.dto.SolicitudParaLambdaDto;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static co.com.zenway.usecase.solicitud.utils.ConstantesAsesor.CORREO_POR_DEFECTO;
import static co.com.zenway.usecase.solicitud.utils.ConstantesAsesor.ESTADO_SOLICITUD_APROBADO;

@RequiredArgsConstructor
public class AsesorSolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioServiceRepository usuarioServiceRepository;
    private final EventosSQSRepository eventosSQSRepository;

    public Flux<SolicitudParaLambdaDto> buscarSolicitudesPendientes(
            List<String> estadosSolicitud,
            String tipoPrestamoNombre,
            int page,
            int size
    ) {
        int offset = page * size;

        List<String> filtrosEstados = (estadosSolicitud == null || estadosSolicitud.isEmpty())
                ? List.of("__VACIO__")
                : estadosSolicitud;

        int estadosContador = (estadosSolicitud == null) ? 0 : estadosSolicitud.size();

        return solicitudRepository.obtenerSolicitudesPendientes(filtrosEstados, estadosContador, tipoPrestamoNombre, size, offset)
                .collectList()
                .flatMapMany(solicitudesPendientesList -> {
                    List<String> emails = solicitudesPendientesList.stream()
                            .map(SolicitudParaLambdaDto::getEmail)
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



    public Mono<Solicitud> actualizarEstadoSolicitud(Long solicitudId, Short nuevoEstadoId){
        return solicitudRepository.actualizarEstadoSolicitud(solicitudId, nuevoEstadoId)
                .flatMap(filasActualizadas -> {
                    if(filasActualizadas > 0){
                        return solicitudRepository.obtenerSolicitudPorId(solicitudId)
                                .flatMap(solicitud -> {
                                    if(nuevoEstadoId.equals(ESTADO_SOLICITUD_APROBADO)){
                                        return eventosSQSRepository
                                                .enviarNotificacionDeEstadoCredito(
                                                        buildMensajeAprobacion(solicitudId, CORREO_POR_DEFECTO, solicitud.getMonto()))
                                                .thenReturn(solicitud);
                                    }
                                    return eventosSQSRepository
                                            .enviarNotificacionDeEstadoCredito(
                                                    buildMensajeRechazo(solicitudId, CORREO_POR_DEFECTO, solicitud.getMonto()))
                                            .thenReturn(solicitud);
                                });

                    }

                    return Mono.error(new IllegalStateException("No se actualiza ninguna solicitud"));
                });
    }


    private MensajeCambioEstadoSolicitudSQSDto buildMensajeAprobacion(Long solicitudId, String email, BigDecimal monto) {
        return new MensajeCambioEstadoSolicitudSQSDto(
                solicitudId,
                email,
                "Prueba de aprobación",
                "¡Tu crédito por el monto de:" + monto +  "fue aprobado!"
        );
    }

    private MensajeCambioEstadoSolicitudSQSDto buildMensajeRechazo(Long solicitudId, String email, BigDecimal monto) {
        return new MensajeCambioEstadoSolicitudSQSDto(
                solicitudId,
                email,
                "Prueba de rechazo",
                "Lo sentimos, tu crédito por el monto de:" + monto +" fue rechazado."
        );
    }




}

