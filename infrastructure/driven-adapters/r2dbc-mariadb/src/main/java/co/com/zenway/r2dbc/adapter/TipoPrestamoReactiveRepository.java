package co.com.zenway.r2dbc.adapter;

import co.com.zenway.r2dbc.entity.TipoPrestamoEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface TipoPrestamoReactiveRepository extends ReactiveCrudRepository<TipoPrestamoEntity, Short>, ReactiveQueryByExampleExecutor<TipoPrestamoEntity>  {

}



