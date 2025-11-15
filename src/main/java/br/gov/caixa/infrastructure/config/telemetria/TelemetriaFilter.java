package br.gov.caixa.infrastructure.config.telemetria;

import br.gov.caixa.domain.port.out.TelemetriaRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;

@Provider
public class TelemetriaFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    TelemetriaRepository telemetriaRepository;

    private static final String START_TIME = "start-time";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME, System.currentTimeMillis());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        long inicio = (long) requestContext.getProperty(START_TIME);
        double tempoRespostaMs = System.currentTimeMillis() - inicio;

        String path = requestContext.getUriInfo().getPath();
        telemetriaRepository.registrarChamada(path, tempoRespostaMs, java.time.LocalDate.now());
    }
}
