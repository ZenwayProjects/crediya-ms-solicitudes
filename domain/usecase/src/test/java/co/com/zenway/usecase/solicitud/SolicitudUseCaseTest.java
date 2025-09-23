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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudUseCaseTest {

    private SolicitudRepository solicitudRepository;
    private UsuarioServiceRepository usuarioServiceRepository;
    private TipoPrestamoRepository tipoPrestamoRepository;
    private EventosSQSRepository eventosSQSRepository;
    private SolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        usuarioServiceRepository = mock(UsuarioServiceRepository.class);
        tipoPrestamoRepository = mock(TipoPrestamoRepository.class);
        eventosSQSRepository = mock(EventosSQSRepository.class);
        useCase = new SolicitudUseCase(solicitudRepository, usuarioServiceRepository, tipoPrestamoRepository, eventosSQSRepository);
    }

    @Test
    void registrarSolicitud_ok_sinValidacionAutomatica() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamoId((short) 1);
        solicitud.setMonto(BigDecimal.valueOf(500));

        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setMontoMinimo(BigDecimal.valueOf(100));
        tipo.setMontoMaximo(BigDecimal.valueOf(1000));
        tipo.setValidacionAutomatica(false);

        UsuarioInfoSolicitudDTO usuarioInfo = new UsuarioInfoSolicitudDTO(125L, "pepe", "test@mail.com", BigDecimal.valueOf(7500000));

        when(tipoPrestamoRepository.existsById((short) 1)).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.buscarPorId((short) 1)).thenReturn(Mono.just(tipo));
        when(usuarioServiceRepository.obtenerUsuarioInfoPorDocumento("123"))
                .thenReturn(Mono.just(usuarioInfo));
        when(solicitudRepository.guardarSolicitudDePrestamo(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitud));

        StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                .expectNextMatches(s -> s.getEmail().equals("test@mail.com") &&
                        s.getEstadoId().equals((short) 1))
                .verifyComplete();

        verify(solicitudRepository).guardarSolicitudDePrestamo(any(Solicitud.class));
        verify(eventosSQSRepository, never()).enviarSolicitudDePrestamoConAutoValidacion(any(), any());
    }

    @Test
    void registrarSolicitud_ok_conValidacionAutomatica() {
            // Arrange
            Solicitud solicitud = new Solicitud();
            solicitud.setId(99L);
            solicitud.setTipoPrestamoId((short) 1);
            solicitud.setMonto(BigDecimal.valueOf(500));
            solicitud.setPlazo(12);

            TipoPrestamo tipo = new TipoPrestamo();
            tipo.setMontoMinimo(BigDecimal.valueOf(100));
            tipo.setMontoMaximo(BigDecimal.valueOf(1000));
            tipo.setValidacionAutomatica(true);
            tipo.setNombre("Libre Inversión");
            tipo.setTasaInteres(BigDecimal.valueOf(0.05));

            SolicitudParaLambdaDto aprobada = new SolicitudParaLambdaDto(
                    1L,
                    BigDecimal.valueOf(500),
                    12,
                    "asesor@mail.com",
                    "pepe",
                    "Libre Inversión",
                    BigDecimal.valueOf(0.05),
                    BigDecimal.valueOf(7500000),
                    "APROBADA",
                    null
            );

            UsuarioInfoSolicitudDTO usuarioInfo = new UsuarioInfoSolicitudDTO(
                    125L,
                    "pepe",
                    "test@mail.com",
                    BigDecimal.valueOf(7500000)
            );

            when(tipoPrestamoRepository.existsById((short) 1)).thenReturn(Mono.just(true));
            when(tipoPrestamoRepository.buscarPorId((short) 1)).thenReturn(Mono.just(tipo));
            when(usuarioServiceRepository.obtenerUsuarioInfoPorDocumento("123"))
                    .thenReturn(Mono.just(usuarioInfo));
            when(solicitudRepository.guardarSolicitudDePrestamo(any(Solicitud.class)))
                    .thenReturn(Mono.just(solicitud));
            when(solicitudRepository.obtenerSolicitudesAprobadasDelUsuario("test@mail.com"))
                    .thenReturn(Flux.just(aprobada));
            when(eventosSQSRepository.enviarSolicitudDePrestamoConAutoValidacion(
                    any(SolicitudParaLambdaDto.class),
                    anyList()
            )).thenReturn(Mono.empty());

            // Act + Assert
            StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                    .expectNextMatches(s -> s.getEmail().equals("test@mail.com") &&
                            s.getEstadoId().equals((short) 1))
                    .verifyComplete();

            verify(eventosSQSRepository).enviarSolicitudDePrestamoConAutoValidacion(any(), any());
    }

    @Test
    void registrarSolicitud_tipoPrestamoNoExiste() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamoId((short) 1);

        when(tipoPrestamoRepository.existsById((short) 1)).thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.buscarPorId((short) 1)).thenReturn(Mono.empty());

        StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                .expectErrorMatches(e -> e instanceof TipoSolicitudInvalido &&
                        e.getMessage().equals(ConstantesExceptions.TIPO_SOLICITUD_INVALIDO))
                .verify();

        verify(solicitudRepository, never()).guardarSolicitudDePrestamo(any());
    }

    @Test
    void registrarSolicitud_montoNoValido() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamoId((short) 1);
        solicitud.setMonto(BigDecimal.valueOf(200000000));

        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setMontoMinimo(BigDecimal.valueOf(100));
        tipo.setMontoMaximo(BigDecimal.valueOf(1000));

        when(tipoPrestamoRepository.existsById((short) 1)).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.buscarPorId((short) 1)).thenReturn(Mono.just(tipo));
        when(usuarioServiceRepository.obtenerUsuarioInfoPorDocumento("123"))
                .thenReturn(Mono.just(new UsuarioInfoSolicitudDTO(1L, "pepe", "dummy@mail.com", BigDecimal.valueOf(7500000))));

        StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                .expectErrorMatches(e -> e instanceof MontoNoValido &&
                        e.getMessage().equals(ConstantesExceptions.MONTO_DEL_PRESTAMO_INVALIDO))
                .verify();

        verify(solicitudRepository, never()).guardarSolicitudDePrestamo(any());
    }
}

