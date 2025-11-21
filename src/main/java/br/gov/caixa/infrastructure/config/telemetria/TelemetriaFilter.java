package br.gov.caixa.infrastructure.config.telemetria;

import br.gov.caixa.domain.port.out.TelemetriaRepository;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;

import java.io.IOException;
import java.time.LocalDate;

@Provider
public class TelemetriaFilter implements ContainerRequestFilter, ContainerResponseFilter {

    @Inject
    TelemetriaRepository telemetriaRepository;

    private static final String START_TIME = "start-time";

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        requestContext.setProperty(START_TIME, System.nanoTime());
    }

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object inicioObj = requestContext.getProperty(START_TIME);
        if (inicioObj == null) {
            return;
        }

        long inicio = (long) inicioObj;
        long duracaoNs = System.nanoTime() - inicio;

        double duracaoMs = duracaoNs / 1_000_000.0;

        String path = requestContext.getUriInfo().getPath();
        telemetriaRepository.registrarChamada(path, duracaoMs, LocalDate.now());
    }
}
