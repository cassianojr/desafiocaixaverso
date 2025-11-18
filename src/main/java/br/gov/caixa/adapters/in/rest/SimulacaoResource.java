package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoResultado;
import br.gov.caixa.domain.port.in.SimularInvestimentoUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/simular-investimento")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SimulacaoResource {

    @Inject
    SimularInvestimentoUseCase simularInvestimento;

    @POST
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
        public Response simularInvestimento(Simulacao simulacao) {
        SimulacaoResultado resultadoSimulacao = simularInvestimento.executar(simulacao);

        return Response.status(Response.Status.OK).entity(resultadoSimulacao).build();
    }
}
