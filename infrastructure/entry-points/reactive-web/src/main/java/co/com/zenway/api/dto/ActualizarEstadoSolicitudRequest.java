package co.com.zenway.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ActualizarEstadoSolicitudRequest {
    private Long idSolicitud;
    private Short estadoNuevo;
}

