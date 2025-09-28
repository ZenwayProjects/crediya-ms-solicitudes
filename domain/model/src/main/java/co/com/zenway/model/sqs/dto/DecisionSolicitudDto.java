package co.com.zenway.model.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionSolicitudDto {
    private Long idSolicitud;
    private String email;
    private BigDecimal monto;
    private String decision; // "APROBADO", "RECHAZADO", "REVISION MANUAL"
}

