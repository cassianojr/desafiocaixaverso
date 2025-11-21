package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.in.ConsultarPerfilRiscoUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/v1/perfil-risco")
@Produces(MediaType.APPLICATION_JSON)
public class PerfilRiscoResource {
    @Inject
    ConsultarPerfilRiscoUseCase consultarPerfilRisco;

    @GET
    @Path("/{clienteId}")
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    public PerfilRiscoResultado consultarPerfilRisco(@PathParam("clienteId") Long clienteId) {
        return consultarPerfilRisco.consultar(clienteId);
    }
}
