package co.com.zenway.model.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DecisionSolicitudDto {
    private Long idSolicitud;
    private String email;
    private String decision; // "APROBADO", "RECHAZADO", "REVISION MANUAL"
}

