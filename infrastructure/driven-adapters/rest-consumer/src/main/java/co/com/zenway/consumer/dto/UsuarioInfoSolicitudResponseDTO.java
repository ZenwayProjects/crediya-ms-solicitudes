package co.com.zenway.consumer.dto;


import java.math.BigDecimal;

public record UsuarioInfoSolicitudResponseDTO(
        Long id,
        String nombre,
        String email,
        BigDecimal salarioBase
) {

}
