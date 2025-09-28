package co.com.zenway.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudRegistroDTO {

    @Schema(example = "140000")
    @NotNull(message = "El monto no puede estar vacio")
    private BigDecimal monto;

    @Schema(example = "7")
    @NotNull(message = "El plazo no puede estar vacio")
    private Integer plazo;

    @NotNull(message = "El documento no puede ser nulo")
    @NotBlank(message = "El documento no puede estar vacio")
    @Schema(example = "100672934")
    private String documentoIdentidad;

    @Schema(example = "1")
    @NotNull(message = "El tipo de prestamo no puede ser nulo")
    private Short tipoPrestamoId;

    
}
