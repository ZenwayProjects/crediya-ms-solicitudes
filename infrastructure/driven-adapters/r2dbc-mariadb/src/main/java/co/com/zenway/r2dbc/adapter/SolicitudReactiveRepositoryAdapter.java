package co.com.zenway.r2dbc.adapter;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.DeudaTotalAprobadaPorUsuarioDto;
import co.com.zenway.model.solicitud.dto.SolicitudParaLambdaDto;
import co.com.zenway.model.solicitud.gateways.SolicitudRepository;
import co.com.zenway.r2dbc.entity.SolicitudEntity;
import co.com.zenway.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class SolicitudReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Solicitud/* change for domain model */,
        SolicitudEntity/* change for adapter model */,
    Long,
        SolicitudReactiveRepository
> implements SolicitudRepository {
            private final TransactionalOperator transactionalOperator;
    private final R2dbcEntityTemplate template;
    public SolicitudReactiveRepositoryAdapter(SolicitudReactiveRepository repository, ObjectMapper mapper, TransactionalOperator transactionalOperator, R2dbcEntityTemplate template) {
        /**
         *  Could be use mapper.mapBuilder if your domain model implement builder pattern
         *  super(repository, mapper, d -> mapper.mapBuilder(d,ObjectModel.ObjectModelBuilder.class).build());
         *  Or using mapper.map with the class of the object model
         */
        super(repository, mapper, d -> mapper.map(d, Solicitud.class/* change for domain model */));
        this.transactionalOperator = transactionalOperator;
        this.template = template;
    }


    @Override
    public Mono<Solicitud> guardarSolicitudDePrestamo(Solicitud solicitud) {
        return this.save(solicitud).as(transactionalOperator::transactional);
    }

    @Override
    public Flux<SolicitudParaLambdaDto> obtenerSolicitudesPendientes(List<String> estadoSolicitud, int estadosContador,
                                                                     String tipoPrestamoNombre, int limit, int offset) {
        return repository.obtenerSolicitudesPorEstadoQuery(estadoSolicitud, estadosContador, tipoPrestamoNombre, limit, offset);
    }

    public Flux<DeudaTotalAprobadaPorUsuarioDto> obtenerSumaDeudaTotalPorEmails(List<String> emails) {
        return repository.obtenerDeudaTotalAprobadaQuery(emails);
    }

    @Override
    public Mono<Solicitud> obtenerSolicitudPorId(Long solicitudId) {
        return repository.findById(solicitudId)
                .map(entity -> mapper.map(entity, Solicitud.class));
    }

    @Override
    public Mono<Long> actualizarEstadoSolicitud(Long solicitudId, Short nuevoEstado) {
        return template.getDatabaseClient()
                .sql("""
                    UPDATE solicitud
                    SET id_estado = :nuevoEstado
                    WHERE id_solicitud = :solicitudId
                    AND id_estado = 1
                """)
                .bind("nuevoEstado", nuevoEstado)
                .bind("solicitudId", solicitudId)
                .fetch()
                .rowsUpdated();
    }

    @Override
    public Flux<SolicitudParaLambdaDto> obtenerSolicitudesAprobadasDelUsuario(String email) {
        return repository.obtenerSolicitudesAprobadasPorUsuario(email);
    }


}
