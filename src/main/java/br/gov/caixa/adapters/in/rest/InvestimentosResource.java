package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.in.ConsultarInvestimentosUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/investimentos")
@Produces(MediaType.APPLICATION_JSON)
public class InvestimentosResource {

    @Inject
    ConsultarInvestimentosUseCase consultarInvestimentosUseCase;

    @GET
    @Path("{clienteId}")
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    public Response consultarInvestimento(@PathParam("clienteId") Long clienteId) {
        return Response.ok(consultarInvestimentosUseCase.consultar(clienteId)).build();
    }
}
