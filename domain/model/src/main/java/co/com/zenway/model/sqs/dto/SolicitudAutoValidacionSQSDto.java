package co.com.zenway.model.sqs.dto;

import co.com.zenway.model.solicitud.Solicitud;
import co.com.zenway.model.solicitud.dto.SolicitudParaLambdaDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SolicitudAutoValidacionSQSDto {
    private SolicitudParaLambdaDto solicitudParaLambdaDto;
    private List<SolicitudParaLambdaDto> solicitudesAprobadas;
}

