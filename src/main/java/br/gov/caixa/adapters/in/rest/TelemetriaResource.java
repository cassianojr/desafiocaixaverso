package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.TelemetriaResultado;
import br.gov.caixa.domain.port.in.ConsultarTelemetriaUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/telemetria")
@Produces(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    @Inject
    ConsultarTelemetriaUseCase consultarTelemetriaUseCase;

    @GET
    @Resource(name = "Consultar Telemetria")
    @APIResponse(
            responseCode = "200",
            description = "Dados de telemetria consultados com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = TelemetriaResultado.class
                    )
            )
    )
    @RolesAllowed({PerfisPermitidos.ADMIN})
    public Response consultarTelemetria() {
        return Response.ok(consultarTelemetriaUseCase.consultar()).build();
    }
}
