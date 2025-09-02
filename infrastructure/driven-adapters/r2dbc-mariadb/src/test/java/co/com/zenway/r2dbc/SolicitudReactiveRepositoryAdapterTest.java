package co.com.zenway.r2dbc;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.r2dbc.adapter.SolicitudReactiveRepository;
import co.com.zenway.r2dbc.adapter.SolicitudReactiveRepositoryAdapter;
import co.com.zenway.r2dbc.entity.SolicitudEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SolicitudReactiveRepositoryAdapterTest {

    @InjectMocks
    SolicitudReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    SolicitudReactiveRepository repository;

    @Mock
    ObjectMapper mapper;


    @Test
    void mustFindValueById() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(1L);

        Solicitud domain = new Solicitud();
        domain.setId(1L);

        when(repository.findById(1L)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Solicitud.class)).thenReturn(domain);

        Mono<Solicitud> result = repositoryAdapter.findById(1L);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    void mustFindAllValues() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(2L);

        Solicitud domain = new Solicitud();
        domain.setId(2L);

        when(repository.findAll()).thenReturn(Flux.just(entity));
        when(mapper.map(entity, Solicitud.class)).thenReturn(domain);

        Flux<Solicitud> result = repositoryAdapter.findAll();

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(2L))
                .verifyComplete();
    }

    @Test
    void mustFindByExample() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(3L);

        Solicitud domain = new Solicitud();
        domain.setId(3L);

        when(repository.findAll(ArgumentMatchers.any()))
                .thenReturn(Flux.just(entity));
        when(mapper.map(entity, Solicitud.class)).thenReturn(domain);

        Flux<Solicitud> result = repositoryAdapter.findByExample(domain);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(3L))
                .verifyComplete();
    }

    @Test
    void mustSaveValue() {
        SolicitudEntity entity = new SolicitudEntity();
        entity.setId(4L);

        Solicitud domain = new Solicitud();
        domain.setId(4L);

        // Simular que el mapper transforma dominio → entidad y entidad → dominio
        when(mapper.map(domain, SolicitudEntity.class)).thenReturn(entity);
        when(repository.save(entity)).thenReturn(Mono.just(entity));
        when(mapper.map(entity, Solicitud.class)).thenReturn(domain);

        Mono<Solicitud> result = repositoryAdapter.save(domain);

        StepVerifier.create(result)
                .expectNextMatches(value -> value.getId().equals(4L))
                .verifyComplete();
    }
}
