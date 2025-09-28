package co.com.zenway.model.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudAprobadaEvent {
    private Long idSolicitud;
    private BigDecimal monto;
    private String fechaAprobacion;
}
