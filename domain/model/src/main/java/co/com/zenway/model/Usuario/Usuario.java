package co.com.zenway.model.Usuario;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Usuario {

    private String nombreCompleto;

    private String email;

    private BigDecimal salarioBase;

}
