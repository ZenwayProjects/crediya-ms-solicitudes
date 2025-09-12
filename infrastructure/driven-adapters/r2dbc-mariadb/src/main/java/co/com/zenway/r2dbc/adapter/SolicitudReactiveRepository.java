package co.com.zenway.r2dbc.adapter;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.DeudaTotalAprobadaPorUsuarioDto;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import co.com.zenway.r2dbc.entity.SolicitudEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;

public interface SolicitudReactiveRepository extends ReactiveCrudRepository<SolicitudEntity, Long>, ReactiveQueryByExampleExecutor<SolicitudEntity> {

    @Query("""
            SELECT
        s.monto,
        s.plazo,
        s.email,
        tp.nombre AS tipo_prestamo,
        tp.tasa_interes AS tasa_interes,
        e.nombre  AS estado_solicitud
    FROM solicitud s
    JOIN tipo_prestamo tp ON tp.id_tipo_prestamo = s.id_tipo_prestamo
    JOIN estados e        ON e.id_estado        = s.id_estado
    WHERE e.nombre IN (:estados)
      AND (:tipoPrestamoNombre IS NULL OR LOWER(tp.nombre) LIKE LOWER(CONCAT('%', :tipoPrestamoNombre, '%')))
    ORDER BY s.id_solicitud ASC LIMIT :limit OFFSET :offset;

    """)
    Flux<SolicitudesPendientesDto> obtenerSolicitudesPendientesQuery(
            @Param("estados")List<String> estadosSolicitudNombre,
            @Param("tipoPrestamoNombre") String tipoPrestamoNombre,
            @Param("limit") int limit,
            @Param("offset") int offset
            );

    @Query("""
        SELECT s.email,
               COALESCE(SUM(s.monto),0) AS deuda_total_aprobada
        FROM solicitud s
        JOIN estados e ON s.id_estado = e.id_estado
        WHERE e.nombre = 'Aprobado'
          AND s.email IN (:emails)
        GROUP BY s.email
       """)
    Flux<DeudaTotalAprobadaPorUsuarioDto> obtenerDeudaTotalAprobadaQuery(
            @Param("emails") List<String> emails);







}
