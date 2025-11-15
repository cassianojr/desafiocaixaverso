package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.in.ConsultarSimulacoesPorProdutoDiaUseCase;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesUseCase;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/simulacoes")
@Produces(MediaType.APPLICATION_JSON)
public class SimulacoesResource {

    @Inject
    ConsultarSimulacoesUseCase consultarSimulacoesUse;

    @Inject
    ConsultarSimulacoesPorProdutoDiaUseCase consultarSimulacoesPorProdutoDia;

    @GET
    public Response listarSimulacoes() {
        var simulacoes = consultarSimulacoesUse.listarTodas();
        return Response.ok(simulacoes).build();
    }

    @GET
    @Path("/por-produto-dia")
    public Response consultarSimulacoes() {
        return Response.ok(consultarSimulacoesPorProdutoDia.consultar()).build();
    }
}
