package co.com.zenway.usecase.solicitud;

import co.com.zenway.model.Usuario.Usuario;
import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.DeudaTotalAprobadaPorUsuarioDto;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.model.solicitud.gateways.UsuarioServiceRepository;
import co.com.zenway.model.sqs.gateways.MensajeSQSRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;

class AsesorSolicitudUseCaseTest {

    private SolicitudRepository solicitudRepository;
    private UsuarioServiceRepository usuarioServiceRepository;
    private MensajeSQSRepository mensajeSQSRepository;
    private AsesorSolicitudUseCase useCase;

    @BeforeEach
    void setUp() {
        solicitudRepository = mock(SolicitudRepository.class);
        usuarioServiceRepository = mock(UsuarioServiceRepository.class);
        mensajeSQSRepository = mock(MensajeSQSRepository.class);
        useCase = new AsesorSolicitudUseCase(solicitudRepository, usuarioServiceRepository, mensajeSQSRepository);
    }

    @Test
    void buscarSolicitudesPendientes_conDatosDeUsuarioYDeuda() {
        SolicitudesPendientesDto dto1 = new SolicitudesPendientesDto();
        dto1.setEmail("a@mail.com");
        SolicitudesPendientesDto dto2 = new SolicitudesPendientesDto();
        dto2.setEmail("b@mail.com");

        Usuario usuarioA = new Usuario();
        usuarioA.setEmail("a@mail.com");
        usuarioA.setNombreCompleto("User A");
        usuarioA.setSalarioBase(BigDecimal.valueOf(2000));

        DeudaTotalAprobadaPorUsuarioDto deudaB = new DeudaTotalAprobadaPorUsuarioDto("b@mail.com", BigDecimal.valueOf(500));

        when(solicitudRepository.obtenerSolicitudesPendientes(anyList(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(Flux.just(dto1, dto2));
        when(usuarioServiceRepository.buscarUsuariosPorEmail(anyList()))
                .thenReturn(Flux.just(usuarioA));
        when(solicitudRepository.obtenerSumaDeudaTotalPorEmails(anyList()))
                .thenReturn(Flux.just(deudaB));

        StepVerifier.create(useCase.buscarSolicitudesPendientes(List.of("1"), "Personal", 0, 10))
                .expectNextMatches(r -> r.getEmail().equals("a@mail.com") &&
                        r.getNombre().equals("User A") &&
                        r.getSalarioBase().equals(BigDecimal.valueOf(2000)) &&
                        r.getDeudaTotalAprobada().equals(BigDecimal.ZERO)) // A no tenía deuda
                .expectNextMatches(r -> r.getEmail().equals("b@mail.com") &&
                        r.getDeudaTotalAprobada().equals(BigDecimal.valueOf(500)))
                .verifyComplete();
    }

    @Test
    void buscarSolicitudesPendientes_sinEmails() {
        SolicitudesPendientesDto dto1 = new SolicitudesPendientesDto();
        dto1.setEmail(null);

        when(solicitudRepository.obtenerSolicitudesPendientes(anyList(), anyInt(), any(), anyInt(), anyInt()))
                .thenReturn(Flux.just(dto1));

        StepVerifier.create(useCase.buscarSolicitudesPendientes(List.of(), "Personal", 0, 10))
                .expectNext(dto1)
                .verifyComplete();

        verify(usuarioServiceRepository, never()).buscarUsuariosPorEmail(anyList());
        verify(solicitudRepository, never()).obtenerSumaDeudaTotalPorEmails(anyList());
    }


    @Test
    void actualizarEstadoSolicitud_aprobado_enviaMensajeAprobacion() {
        Long solicitudId = 1L;
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);
        solicitud.setMonto(BigDecimal.valueOf(1000));

        when(solicitudRepository.actualizarEstadoSolicitud(solicitudId, (short) 2))
                .thenReturn(Mono.just(1L));
        when(solicitudRepository.obtenerSolicitudPorId(solicitudId))
                .thenReturn(Mono.just(solicitud));
        when(mensajeSQSRepository.enviarNotificacionDeEstadoCredito(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.actualizarEstadoSolicitud(solicitudId, (short) 2))
                .expectNext(solicitud)
                .verifyComplete();

        verify(mensajeSQSRepository).enviarNotificacionDeEstadoCredito(
                argThat(m -> m.getAsunto().contains("aprobación")));
    }

    @Test
    void actualizarEstadoSolicitud_rechazado_enviaMensajeRechazo() {
        Long solicitudId = 2L;
        Solicitud solicitud = new Solicitud();
        solicitud.setId(solicitudId);
        solicitud.setMonto(BigDecimal.valueOf(2000));

        when(solicitudRepository.actualizarEstadoSolicitud(solicitudId, (short) 3))
                .thenReturn(Mono.just(1L));
        when(solicitudRepository.obtenerSolicitudPorId(solicitudId))
                .thenReturn(Mono.just(solicitud));
        when(mensajeSQSRepository.enviarNotificacionDeEstadoCredito(any()))
                .thenReturn(Mono.empty());

        StepVerifier.create(useCase.actualizarEstadoSolicitud(solicitudId, (short) 3))
                .expectNext(solicitud)
                .verifyComplete();

        verify(mensajeSQSRepository).enviarNotificacionDeEstadoCredito(
                argThat(m -> m.getAsunto().contains("rechazo")));
    }

    @Test
    void actualizarEstadoSolicitud_sinFilasAfectadas_lanzaError() {
        when(solicitudRepository.actualizarEstadoSolicitud(99L, (short) 3))
                .thenReturn(Mono.just(0L));

        StepVerifier.create(useCase.actualizarEstadoSolicitud(99L, (short) 3))
                .expectErrorMatches(e -> e instanceof IllegalStateException &&
                        e.getMessage().contains("No se actualiza ninguna solicitud"))
                .verify();

        verify(solicitudRepository, never()).obtenerSolicitudPorId(anyLong());
        verify(mensajeSQSRepository, never()).enviarNotificacionDeEstadoCredito(any());
    }

}
