package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoResultado;
import br.gov.caixa.domain.port.in.SimularInvestimentoUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import io.quarkus.cache.CacheResult;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimularInvestimentoUseCase simularInvestimento;

    @POST
    @Resource(name = "Simular Investimento")
    @APIResponse(
            responseCode = "200",
            description = "Simulação de investimento realizada com sucesso",
            content = @Content(
                mediaType = MediaType.APPLICATION_JSON,
                schema = @Schema(
                        implementation = SimulacaoResultado.class,
                        type = SchemaType.OBJECT
                )
            )
    )
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    @CacheResult(cacheName = "simulacaoInvestimentoCache")
    public Response simularInvestimento(
            @RequestBody(
                    description = "Parâmetros para simulação de investimento",
                    required = true,
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON,
                            schema = @Schema(
                                    implementation = Simulacao.class,
                                    type = SchemaType.OBJECT
                            )
                    )
            )
            Simulacao simulacao) {
        SimulacaoResultado resultadoSimulacao = simularInvestimento.executar(simulacao);

        return Response.status(Response.Status.OK).entity(resultadoSimulacao).build();
    }
}
