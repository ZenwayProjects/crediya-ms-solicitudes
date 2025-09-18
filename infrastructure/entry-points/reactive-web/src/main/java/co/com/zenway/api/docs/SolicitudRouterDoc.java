package co.com.zenway.api.docs;

import co.com.zenway.api.SolicitudHandler;
import co.com.zenway.api.dto.ActualizarEstadoSolicitudRequest;
import co.com.zenway.api.dto.SolicitudRegistroDTO;
import co.com.zenway.api.dto.SolicitudResponseDTO;
import co.com.zenway.model.solicitud.dto.SolicitudesPendientesDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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

import static org.springframework.web.reactive.function.server.RequestPredicates.*;
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
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Solicitud inválida, datos incorrectos o incompletos"
                            ),
                            @ApiResponse(
                                    responseCode = "401",
                                    description = "Usuario no autenticado"
                            ),
                            @ApiResponse(
                                    responseCode = "403",
                                    description = "Acceso denegado, el usuario no tiene permisos para realizar esta acción"
                            ),
                    }
            )
    )
    public RouterFunction<ServerResponse> usuarioRoutes(SolicitudHandler solicitudHandler) {
        return route(POST("/api/v1/solicitud"), solicitudHandler::solicitarCredito);
    }

    @Bean
    @RouterOperation(
            path = "/api/v1/solicitudes/pendientes",
            produces = {"application/json; charset=UTF-8"},
            method = RequestMethod.GET,
            beanClass = SolicitudHandler.class,
            beanMethod = "listarSolicitudesPendientes",
            operation = @Operation(
                    operationId = "listarSolicitudesPendientes",
                    parameters = {
                            @Parameter(name = "page", description = "Page number", example = "1", required = true),
                            @Parameter(name = "size", description = "Size number", example = "10", required = true),
                    },
                    summary = "Lista las solicitudes pendientes",
                    description = "Devuelve un listado paginado de solicitudes pendientes con información adicional del usuario",
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Listado de solicitudes pendientes",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = SolicitudesPendientesDto.class)
                                    )
                            ),
                            @ApiResponse(responseCode = "401", description = "Usuario no autenticado"),
                            @ApiResponse(responseCode = "403", description = "Acceso denegado"),
                            @ApiResponse(responseCode = "500", description = "Error interno del servidor")
                    }
            )
    )
    public RouterFunction<ServerResponse> listarSolicitudesPendientesRoute(SolicitudHandler solicitudHandler) {
        return route(GET("/api/v1/solicitud"), solicitudHandler::listarSolicitudesPendientes);
    }

    @Bean
    @RouterOperation(
            path = "/api/v1/solicitud",
            produces = {"application/json; charset=UTF-8"},
            method = RequestMethod.PUT, // o POST si prefieres
            beanClass = SolicitudHandler.class,
            beanMethod = "actualizarEstadoSolicitud",
            operation = @Operation(
                    operationId = "actualizarEstadoSolicitud",
                    summary = "Actualiza el estado de una solicitud",
                    description = "Recibe el ID de la solicitud y el nuevo estado, y devuelve la solicitud actualizada",
                    requestBody = @RequestBody(
                            required = true,
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ActualizarEstadoSolicitudRequest.class)
                            )
                    ),
                    responses = {
                            @ApiResponse(
                                    responseCode = "200",
                                    description = "Solicitud actualizada correctamente",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = SolicitudResponseDTO.class)
                                    )
                            ),
                            @ApiResponse(
                                    responseCode = "400",
                                    description = "Solicitud inválida, datos incorrectos o incompletos"
                            ),
                            @ApiResponse(
                                    responseCode = "401",
                                    description = "Usuario no autenticado"
                            ),
                            @ApiResponse(
                                    responseCode = "403",
                                    description = "Acceso denegado, el usuario no tiene permisos para realizar esta acción"
                            ),
                            @ApiResponse(
                                    responseCode = "500",
                                    description = "Error interno del servidor"
                            )
                    }
            )
    )
    public RouterFunction<ServerResponse> actualizarEstadoSolicitudRoute(SolicitudHandler solicitudHandler) {
        return route(PUT("/api/v1/solicitud"), solicitudHandler::actualizarEstadoSolicitud);
    }


}
