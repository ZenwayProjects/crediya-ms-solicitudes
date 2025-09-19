package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.UsuarioInfoSolicitudDTO;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import co.com.zenway.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.zenway.usecase.solicitud.exception.MontoNoValido;
import co.com.zenway.usecase.solicitud.exception.TipoSolicitudInvalido;
import co.com.zenway.usecase.solicitud.utils.ConstantesExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioServiceRepository usuarioServiceRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;


    private static final Short ESTADO_PENDIENTE_REVISION = 1;

    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud, String documentoIdentidad){
        return validarTipoSolicitudPorId(solicitud.getTipoPrestamoId())
                .then(tipoPrestamoRepository.buscarPorId(solicitud.getTipoPrestamoId()))
                .flatMap(tipoPrestamo -> {
                    if(tipoPrestamo.getValidacionAutomatica().equals(Boolean.TRUE)){

                    }
                    if(solicitud.getMonto().compareTo(tipoPrestamo.getMontoMaximo()) > 0 ||
                            solicitud.getMonto().compareTo(tipoPrestamo.getMontoMinimo())< 0){

                        return Mono.error(new MontoNoValido(ConstantesExceptions.MONTO_DEL_PRESTAMO_INVALIDO));
                    }
                    return Mono.empty();
                })
                .then(obtenerUsuarioInfoPorDocumentoIdentidad(documentoIdentidad))
                .flatMap(infoSolicitudDTO -> {
                    solicitud.setEmail(infoSolicitudDTO.email());
                    solicitud.setEstadoId(ESTADO_PENDIENTE_REVISION);
                    return solicitudRepository.enviarSolicitudDePrestamo(solicitud);
                });
    }

    public Mono<UsuarioInfoSolicitudDTO> obtenerUsuarioInfoPorDocumentoIdentidad(String documentoIdentidad) {
        return usuarioServiceRepository.obtenerUsuarioInfoPorDocumento(documentoIdentidad);

    }


    private  Mono<Void> validarTipoSolicitudPorId(Short id){
        return tipoPrestamoRepository.existsById(id)
                .flatMap(exists ->{
                    if(Boolean.FALSE.equals(exists)){
                        return Mono.error(new TipoSolicitudInvalido(ConstantesExceptions.TIPO_SOLICITUD_INVALIDO));
                    }
                    return Mono.empty();
                });
    }


}
