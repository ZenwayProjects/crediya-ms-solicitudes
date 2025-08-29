package co.com.zenway.model.solicitud;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class Solicitud {

    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Short estadoId;
    private Short tipoPrestamoId;


}
