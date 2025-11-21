package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.in.ConsultarPerfilRiscoUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import io.quarkus.cache.CacheResult;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilRiscoResource {
    @Inject
    ConsultarPerfilRiscoUseCase consultarPerfilRisco;

    @GET
    @Operation(summary = "Consultar Perfil de Risco", description = "Consulta o perfil de risco de um cliente pelo ID")
    @APIResponse(
            responseCode = "200",
            description = "Perfil de risco consultado com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = PerfilRiscoResultado.class,
                            type = SchemaType.OBJECT
                    )
            )
    )
    @Path("/{clienteId}")
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    @CacheResult(cacheName = "perfilRiscoCache")
    public Response consultarPerfilRisco(
            @Parameter(description = "ID do cliente", required = true)
            @PathParam("clienteId") Long clienteId) {
        return Response.ok(consultarPerfilRisco.consultar(clienteId)).build();
    }
}
