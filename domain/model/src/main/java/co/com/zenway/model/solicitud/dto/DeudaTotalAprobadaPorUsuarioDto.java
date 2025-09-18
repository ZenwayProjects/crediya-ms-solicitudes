package co.com.zenway.model.solicitud.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeudaTotalAprobadaPorUsuarioDto {
    private String email;
    private BigDecimal deudaTotalAprobada;
}
