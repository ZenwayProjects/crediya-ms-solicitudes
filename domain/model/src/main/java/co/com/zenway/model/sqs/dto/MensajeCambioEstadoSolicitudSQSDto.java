package co.com.zenway.model.sqs.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeCambioEstadoSolicitudSQSDto {

    private Long solicitudId;
    private String email;
    private String asunto;
    private String mensaje;

}
