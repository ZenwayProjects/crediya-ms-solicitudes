package co.com.zenway.model.tipoprestamo;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TipoPrestamo {

    private Short id;

    private String nombre;

    private BigDecimal montoMinimo;

    private BigDecimal montoMaximo;

    private BigDecimal tasaInteres;

    private Boolean validacionAutomatica;



}
