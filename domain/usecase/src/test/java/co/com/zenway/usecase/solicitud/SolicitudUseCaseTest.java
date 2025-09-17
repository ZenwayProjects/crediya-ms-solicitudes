package co.com.zenway.usecase.solicitud;


import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.UsuarioInfoSolicitudDTO;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import co.com.zenway.model.tipoprestamo.TipoPrestamo;
import co.com.zenway.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.zenway.usecase.solicitud.exception.MontoNoValido;
import co.com.zenway.usecase.solicitud.exception.TipoSolicitudInvalido;
import co.com.zenway.usecase.solicitud.utils.ConstantesExceptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SolicitudUseCaseTest {

    private SolicitudRepository solicitudRepository;
    private UsuarioServiceRepository usuarioServiceRepository;
    private TipoPrestamoRepository tipoPrestamoRepository;
    private SolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        usuarioServiceRepository = mock(UsuarioServiceRepository.class);
        tipoPrestamoRepository = mock(TipoPrestamoRepository.class);
        useCase = new SolicitudUseCase(solicitudRepository, usuarioServiceRepository, tipoPrestamoRepository);
    }

    @Test
    void registrarSolicitud_ok() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamoId((short) 1);
        solicitud.setMonto(BigDecimal.valueOf(500));

        TipoPrestamo tipo = new TipoPrestamo();
        tipo.setMontoMinimo(BigDecimal.valueOf(100));
        tipo.setMontoMaximo(BigDecimal.valueOf(1000));

        UsuarioInfoSolicitudDTO usuarioInfo = new UsuarioInfoSolicitudDTO(125L,"test@mail.com");

        when(tipoPrestamoRepository.existsById((short) 1)).thenReturn(Mono.just(true));
        when(tipoPrestamoRepository.buscarPorId((short) 1)).thenReturn(Mono.just(tipo));
        when(usuarioServiceRepository.obtenerUsuarioInfoPorDocumento("123"))
                .thenReturn(Mono.just(usuarioInfo));
        when(solicitudRepository.enviarSolicitudDePrestamo(any(Solicitud.class)))
                .thenReturn(Mono.just(solicitud));

        // Act + Assert
        StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                .expectNextMatches(s -> s.getEmail().equals("test@mail.com") &&
                        s.getEstadoId().equals((short) 1))
                .verifyComplete();

        verify(solicitudRepository).enviarSolicitudDePrestamo(any(Solicitud.class));
    }

    @Test
    void registrarSolicitud_tipoPrestamoNoExiste() {
        Solicitud solicitud = new Solicitud();
        solicitud.setTipoPrestamoId((short) 1);

        when(tipoPrestamoRepository.existsById((short) 1)).thenReturn(Mono.just(false));
        when(tipoPrestamoRepository.buscarPorId((short) 1))
                .thenReturn(Mono.empty());

        when(usuarioServiceRepository.obtenerUsuarioInfoPorDocumento("123"))
                .thenReturn(Mono.just(new UsuarioInfoSolicitudDTO(1L, "dummy@mail.com")));

        StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                .expectErrorMatches(e -> e instanceof TipoSolicitudInvalido &&
                        e.getMessage().equals(ConstantesExceptions.TIPO_SOLICITUD_INVALIDO))
                .verify();

        verify(solicitudRepository, never()).enviarSolicitudDePrestamo(any());
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
                .thenReturn(Mono.just(new UsuarioInfoSolicitudDTO(1L, "dummy@mail.com")));

        StepVerifier.create(useCase.registrarSolicitud(solicitud, "123"))
                .expectErrorMatches(e -> e instanceof MontoNoValido &&
                        e.getMessage().equals(ConstantesExceptions.MONTO_DEL_PRESTAMO_INVALIDO))
                .verify();

        verify(solicitudRepository, never()).enviarSolicitudDePrestamo(any());
    }
}

