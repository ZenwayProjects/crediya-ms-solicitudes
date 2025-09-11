package co.com.zenway.r2dbc.adapter;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.r2dbc.entity.SolicitudEntity;
import co.com.zenway.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud/* change for domain model */,
        SolicitudEntity/* change for adapter model */,
    Long,
        SolicitudReactiveRepository
> implements SolicitudRepository {
            private final TransactionalOperator transactionalOperator;
    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Solicitud.class/* change for domain model */));
        this.transactionalOperator = transactionalOperator;
    }


    @Override
    public Mono<Solicitud> enviarSolicitudDePrestamo(Solicitud solicitud) {
        return this.save(solicitud).as(transactionalOperator::transactional);
    }

    @Override
    public Flux<SolicitudesPendientesDto> obtenerSolicitudesPendientes(List<String> estadoSolicitud,
                                                                       String tipoPrestamoNombre, int limit, int offset) {
        return repository.obtenerSolicitudesPendientesQuery(estadoSolicitud, tipoPrestamoNombre, limit, offset);
    }

    @Override
    public Mono<BigDecimal> obtenerSumaDeudaTotal(String email) {
        return repository.obtenerDeudaTotalAprobadaQuery(email);
    }


}
