package co.com.zenway.consumer.dto;

import jakarta.validation.constraints.Email;
import lombok.Data;

public record EmailRecibidoDTO(
        @Email
        String email
) {


}
