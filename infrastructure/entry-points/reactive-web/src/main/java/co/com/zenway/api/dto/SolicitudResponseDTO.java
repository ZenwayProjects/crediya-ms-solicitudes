package co.com.zenway.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudResponseDTO {

    @Schema(example = "140000")
    private BigDecimal monto;

    @Schema(example = "7")
    private Integer plazo;

    @Schema(example = "juanito@gmail.com")
    private String email;

    @Schema(example = "1")
    private Short estadoId;

    @Schema(example = "1")
    private Short tipoPrestamoId;
}
