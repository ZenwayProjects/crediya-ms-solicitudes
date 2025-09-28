package co.com.zenway.consumer.mapper;

import co.com.zenway.consumer.dto.UsuarioResponseDTO;
import co.com.zenway.model.Usuario.Usuario;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import reactor.core.publisher.Flux;

@Mapper(componentModel = "spring")
public interface UsuarioMapper {

    @Mapping(target = "nombreCompleto", expression = "java(usuarioResponseDTO.getNombre() + \" \" + usuarioResponseDTO.getApellido())")
    Usuario toDominio(UsuarioResponseDTO usuarioResponseDTO);


}
