package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.in.ConsultarProdutosRecomendadosUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
public class ProdutosRecomendadosResource {

    @Inject
    ConsultarProdutosRecomendadosUseCase consultar;

    @GET
    @Path("/{perfil}")
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    public Response consultar(@PathParam("perfil") String perfil){
        return Response.ok(consultar.consultar(perfil)).build();
    }
}
