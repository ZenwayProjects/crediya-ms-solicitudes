package co.com.zenway.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class SolicitudRegistroDTO {

    @NotNull(message = "El monto no puede estar vacio")
    private BigDecimal monto;

    @NotNull(message = "El plazo no puede estar vacio")
    private Integer plazo;

    @NotNull(message = "El documento no puede ser nulo")
    @NotBlank(message = "El documento no puede estar vacio")
    private String documentoIdentidad;

    @NotNull(message = "El tipo de prestamo no puede ser nulo")
    private Short tipoPrestamoId;

    
}
