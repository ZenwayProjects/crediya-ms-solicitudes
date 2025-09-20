package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.SolicitudParaLambdaDto;
import co.com.zenway.model.solicitud.dto.UsuarioInfoSolicitudDTO;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import co.com.zenway.model.sqs.gateways.EventosSQSRepository;
import co.com.zenway.model.tipoprestamo.TipoPrestamo;
import co.com.zenway.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.zenway.usecase.solicitud.exception.MontoNoValido;
import co.com.zenway.usecase.solicitud.exception.TipoSolicitudInvalido;
import co.com.zenway.usecase.solicitud.utils.ConstantesExceptions;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import static co.com.zenway.usecase.solicitud.utils.ConstantesAsesor.CORREO_POR_DEFECTO;

@RequiredArgsConstructor
public class SolicitudUseCase {

    private final SolicitudRepository solicitudRepository;
    private final UsuarioServiceRepository usuarioServiceRepository;
    private final TipoPrestamoRepository tipoPrestamoRepository;
    private final EventosSQSRepository eventosSQSRepository;


    private static final Short ESTADO_PENDIENTE_REVISION = 1;

    public Mono<Solicitud> registrarSolicitud(Solicitud solicitud, String documentoIdentidad){
        return validarTipoSolicitudPorId(solicitud.getTipoPrestamoId())
                .then(tipoPrestamoRepository.buscarPorId(solicitud.getTipoPrestamoId()))
                .flatMap(tipoPrestamo -> {
                    if(solicitud.getMonto().compareTo(tipoPrestamo.getMontoMaximo()) > 0 ||
                            solicitud.getMonto().compareTo(tipoPrestamo.getMontoMinimo())< 0){

                        return Mono.error(new MontoNoValido(ConstantesExceptions.MONTO_DEL_PRESTAMO_INVALIDO));
                    }
                    return obtenerUsuarioInfoPorDocumentoIdentidad(documentoIdentidad)//aca se obtiene el salarioBase
                            .map(info -> Tuples.of(tipoPrestamo, info));
                })
                .flatMap( tupla -> {
                    var tipoPrestamo = tupla.getT1();
                    var infoSolicitudDTO = tupla.getT2();

                    solicitud.setEmail(infoSolicitudDTO.email());
                    solicitud.setEstadoId(ESTADO_PENDIENTE_REVISION);

                    return solicitudRepository.guardarSolicitudDePrestamo(solicitud)
                            .flatMap(solicitudGuardada ->
                                    procesarAutoValidacionSiRequiere(solicitudGuardada,
                                            tipoPrestamo, infoSolicitudDTO, solicitudGuardada.getEmail())
                                            .then(Mono.just(solicitudGuardada))
                            );
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

    private Flux<SolicitudParaLambdaDto> procesarAutoValidacionSiRequiere(Solicitud solicitud, TipoPrestamo tipoPrestamo, UsuarioInfoSolicitudDTO usuarioInfoSolicitudDTO, String email){
        if(Boolean.TRUE.equals(tipoPrestamo.getValidacionAutomatica())){

            SolicitudParaLambdaDto solicitudNueva = new SolicitudParaLambdaDto(
                    solicitud.getId(),
                    solicitud.getMonto(),
                    solicitud.getPlazo(),
                    CORREO_POR_DEFECTO,
                    null,
                    tipoPrestamo.getNombre(),
                    tipoPrestamo.getTasaInteres(),
                    usuarioInfoSolicitudDTO.salarioBase(),
                    "Pendiente",
                    null
            );

            return solicitudRepository.obtenerSolicitudesAprobadasDelUsuario(email)
                    .collectList()
                    .flatMapMany(solicitudesAprobadas ->
                            eventosSQSRepository.enviarSolicitudDePrestamoConAutoValidacion(solicitudNueva, solicitudesAprobadas)
                                    .doOnSuccess(v -> System.out.println("📤 Mensaje enviado a SQS para solicitud {}" + solicitudNueva.getIdSolicitud()))
                            .thenMany(Flux.fromIterable(solicitudesAprobadas))
                    );
        }
        return Flux.empty();
    }


}
