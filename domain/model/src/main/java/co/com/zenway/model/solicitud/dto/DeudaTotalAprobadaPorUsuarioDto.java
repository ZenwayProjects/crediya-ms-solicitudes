package co.com.zenway.model.solicitud.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DeudaTotalAprobadaPorUsuarioDto {
    private String email;
    private BigDecimal deudaTotalAprobada;
}
