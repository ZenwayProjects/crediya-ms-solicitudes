package co.com.zenway.model.solicitud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudParaLambdaDto {

    private Long idSolicitud;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private String nombre;
    private String tipoPrestamo;
    private BigDecimal tasaInteres;
    private BigDecimal salarioBase;
    private String estadoSolicitud;
    private BigDecimal deudaTotalAprobada;

}
