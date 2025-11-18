package br.gov.caixa.infrastructure.config.telemetria;

import br.gov.caixa.domain.port.out.TelemetriaRepository;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.core.UriInfo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class TelemetriaFilterTest {

    @Inject
    TelemetriaFilter filter;

    @InjectMock
    TelemetriaRepository telemetriaRepository;

    private ContainerRequestContext buildRequestWithPropertyMap(Map<String, Object> props, String path) {
        ContainerRequestContext request = mock(ContainerRequestContext.class);
        doAnswer(inv -> { props.put(inv.getArgument(0), inv.getArgument(1)); return null; })
                .when(request).setProperty(anyString(), any());
        when(request.getProperty(anyString())).thenAnswer(inv -> props.get(inv.getArgument(0)));

        UriInfo uriInfo = mock(UriInfo.class);
        when(uriInfo.getPath()).thenReturn(path);
        when(request.getUriInfo()).thenReturn(uriInfo);
        return request;
    }

    @Test
    @DisplayName("Registra chamada no response quando start-time foi setado no request")
    void registraChamada() throws Exception {
        Map<String, Object> props = new HashMap<>();
        ContainerRequestContext request = buildRequestWithPropertyMap(props, "/simulacoes");
        ContainerResponseContext response = mock(ContainerResponseContext.class);

        filter.filter(request); // seta START_TIME
        filter.filter(request, response); // calcula e registra

        ArgumentCaptor<String> servico = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Double> tempo = ArgumentCaptor.forClass(Double.class);
        ArgumentCaptor<LocalDate> data = ArgumentCaptor.forClass(LocalDate.class);
        verify(telemetriaRepository, times(1)).registrarChamada(servico.capture(), tempo.capture(), data.capture());

        assertEquals("/simulacoes", servico.getValue());
        assertNotNull(tempo.getValue());
        assertTrue(tempo.getValue() >= 0.0);
        assertEquals(LocalDate.now(), data.getValue());
    }

    @Test
    @DisplayName("Não registra quando START_TIME ausente")
    void naoRegistraSemStartTime() throws Exception {
        Map<String, Object> props = new HashMap<>();
        ContainerRequestContext request = buildRequestWithPropertyMap(props, "/investimentos");
        ContainerResponseContext response = mock(ContainerResponseContext.class);

        // não chama filter(request), apenas o response
        filter.filter(request, response);

        verify(telemetriaRepository, never()).registrarChamada(anyString(), anyDouble(), any(LocalDate.class));
    }
}
