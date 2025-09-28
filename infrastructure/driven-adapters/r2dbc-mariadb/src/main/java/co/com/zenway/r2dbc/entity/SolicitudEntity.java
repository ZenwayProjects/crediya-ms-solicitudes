package co.com.zenway.r2dbc.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table(name = "solicitud")
@Data
public class SolicitudEntity {

    @Id
    @Column("id_solicitud")
    private Long id;

    @Column("monto")
    private BigDecimal monto;

    @Column("plazo")
    private Integer plazo;

    @Column("email")
    private String email;

    @Column("id_estado")
    private Short estadoId;

    @Column("id_tipo_prestamo")
    private Short tipoPrestamoId;


}
