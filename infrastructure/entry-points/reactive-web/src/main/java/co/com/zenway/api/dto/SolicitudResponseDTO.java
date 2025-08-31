package co.com.zenway.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudResponseDTO {


    private BigDecimal monto;

    private Integer plazo;

    private String email;

    private Short estadoId;

    private Short tipoPrestamoId;
}
