package co.com.zenway.api.mapper;

import co.com.zenway.api.dto.SolicitudRegistroDTO;
import co.com.zenway.api.dto.SolicitudResponseDTO;
import co.com.zenway.model.solicitud.Solicitud;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SolicitudMapper {

    SolicitudResponseDTO toResponse(Solicitud solicitud);

    Solicitud toModel(SolicitudRegistroDTO solicitudRegistroDTO);
}
