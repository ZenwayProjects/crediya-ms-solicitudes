package co.com.zenway.api.docs;

import co.com.zenway.api.SolicitudHandler;
import co.com.zenway.api.dto.SolicitudRegistroDTO;
import co.com.zenway.api.dto.SolicitudResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class SolicitudRouterDoc {

    @Bean
    @RouterOperation(
            path = "/api/v1/solicitud",
            produces ={"application/json; charset=UTF-8"},
            method = RequestMethod.POST,
            beanClass = SolicitudHandler.class,
            beanMethod = "solicitarCredito",
            operation = @Operation(
                    operationId = "solicitarPrestamo",
                    summary = "Solicita un prestamo con la informacion del usuario",
                    description = "Recibe los datos de la solicitud del prestamo y devuelve la solicitud registrada",
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SolicitudRegistroDTO.class)
                            )
                    ),
                    responses = {
                            @ApiResponse(
                                    responseCode = "201",
                                    description = "Solicitud creada correctamente",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = SolicitudResponseDTO.class)
                                    )
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> usuarioRoutes(SolicitudHandler solicitudHandler) {
        return route(POST("/api/v1/solicitud"), solicitudHandler::solicitarCredito);
    }

}
