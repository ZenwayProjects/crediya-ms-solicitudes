package co.com.zenway.model.solicitud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class DeudaTotalAprobadaPorUsuarioDto {
    private String email;
    private BigDecimal deudaTotalAprobada;
}
