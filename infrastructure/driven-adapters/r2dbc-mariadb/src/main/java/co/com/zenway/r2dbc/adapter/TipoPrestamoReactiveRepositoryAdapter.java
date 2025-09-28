package co.com.zenway.r2dbc.adapter;

import co.com.zenway.model.tipoprestamo.TipoPrestamo;
import co.com.zenway.model.tipoprestamo.gateways.TipoPrestamoRepository;
import co.com.zenway.r2dbc.entity.TipoPrestamoEntity;
import co.com.zenway.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


@Repository
public class TipoPrestamoReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        TipoPrestamo,
        TipoPrestamoEntity,
        Short,
        TipoPrestamoReactiveRepository> implements TipoPrestamoRepository {

    public TipoPrestamoReactiveRepositoryAdapter(TipoPrestamoReactiveRepository repository, ObjectMapper mapper) {

        super(repository, mapper, d -> mapper.map(d, TipoPrestamo.class));

    }

    @Override
    public Mono<Boolean> existsById(Short id) {
        return repository.existsById(id);
    }

    @Override
    public Mono<TipoPrestamo> buscarPorId(Short id) {
        return super.findById(id);
    }


}
