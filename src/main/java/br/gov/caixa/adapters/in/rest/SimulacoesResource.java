package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesPorProdutoDiaUseCase;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
public class SimulacoesResource {

    @Inject
    ConsultarSimulacoesUseCase consultarSimulacoesUse;

    @Inject
    ConsultarSimulacoesPorProdutoDiaUseCase consultarSimulacoesPorProdutoDia;

    @GET
    @Resource(name = "Listar Simulações")
    @APIResponse(
            responseCode = "200",
            description = "Simulações listadas com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = SimulacaoHistorico.class,
                            type = SchemaType.ARRAY
                    )
            )
    )
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    public Response listarSimulacoes() {
        var simulacoes = consultarSimulacoesUse.listarTodas();
        return Response.ok(simulacoes).build();
    }


    @GET
    @Resource(name = "Consultar Simulações por Produto e Dia")
    @APIResponse(
            responseCode = "200",
            description = "Simulações consultadas por produto e dia com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = SimulacaoPorProdutoDia.class,
                            type = SchemaType.ARRAY
                    )
            )
    )
    @Path("/por-produto-dia")
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    public Response consultarSimulacoes() {
        return Response.ok(consultarSimulacoesPorProdutoDia.consultar()).build();
    }
}
