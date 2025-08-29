package co.com.zenway.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudRegistroDTO {

    private BigDecimal monto;

    private Integer plazo;

    private String documentoIdentidad;

   private Short tipoPrestamoId;

    
}
