package co.com.zenway.model.sqs;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MensajeSQSDto {

    private Long solicitudId;
    private String email;
    private String asunto;
    private String mensaje;

}
