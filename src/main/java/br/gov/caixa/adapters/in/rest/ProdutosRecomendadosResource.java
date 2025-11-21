package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.port.in.ConsultarProdutosRecomendadosUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.Resource;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;

@Path("/api/v1/produtos-recomendados")
@Produces(MediaType.APPLICATION_JSON)
public class ProdutosRecomendadosResource {

    @Inject
    ConsultarProdutosRecomendadosUseCase consultar;

    @GET
    @Resource(name = "Consultar Produtos Recomendados por Perfil")
    @APIResponse(
            responseCode = "200",
            description = "Produtos recomendados consultados com sucesso",
            content = @Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = @Schema(
                            implementation = Produto.class,
                            type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY
                    )
            )
    )
    @Path("/{perfil}")
    @RolesAllowed({PerfisPermitidos.ADMIN, PerfisPermitidos.USER})
    public Response consultar(
            @Parameter(description = "Perfil do cliente", required = true)
            @PathParam("perfil") String perfil){
        return Response.ok(consultar.consultar(perfil)).build();
    }
}
