package co.com.zenway.model.solicitud.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudesPendientesDto {

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
