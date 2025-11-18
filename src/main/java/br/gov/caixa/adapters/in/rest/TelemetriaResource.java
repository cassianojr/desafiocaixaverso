package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.in.ConsultarTelemetriaUseCase;
import br.gov.caixa.infrastructure.security.PerfisPermitidos;
import jakarta.annotation.security.RolesAllowed;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/telemetria")
@Produces(MediaType.APPLICATION_JSON)
public class TelemetriaResource {

    @Inject
    ConsultarTelemetriaUseCase consultarTelemetriaUseCase;

    @GET
    @RolesAllowed({PerfisPermitidos.ADMIN})
    public Response consultarTelemetria() {
        return Response.ok(consultarTelemetriaUseCase.consultar()).build();
    }
}
