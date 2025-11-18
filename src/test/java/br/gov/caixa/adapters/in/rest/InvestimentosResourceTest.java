package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.port.in.ConsultarInvestimentosUseCase;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class InvestimentosResourceTest {

    ConsultarInvestimentosUseCase useCase;
    InvestimentosResource resource;

    @BeforeEach
    void setup() {
        useCase = mock(ConsultarInvestimentosUseCase.class);
        resource = new InvestimentosResource();
        resource.consultarInvestimentosUseCase = useCase; // injeção do mock
    }

    @Test
    @DisplayName("GET /investimentos/{id} retorna lista vazia quando cliente sem registros")
    void consultarInvestimentosVazio() {
        when(useCase.consultar(10L)).thenReturn(List.of());

        var response = resource.consultarInvestimento(10L);

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof List<?>);
        assertEquals(0, ((List<?>) response.getEntity()).size());
        verify(useCase).consultar(10L);
    }

    @Test
    @DisplayName("GET /investimentos/{id} retorna lista com investimentos")
    void consultarInvestimentosComDados() {
        var inv1 = new Investimento(1L, 1L, "CDB", BigDecimal.valueOf(1000), BigDecimal.valueOf(0.12), LocalDate.now());
        var inv2 = new Investimento(2L, 1L, "ACOES", BigDecimal.valueOf(2000), BigDecimal.valueOf(0.30), LocalDate.now());
        when(useCase.consultar(1L)).thenReturn(List.of(inv1, inv2));

        var response = resource.consultarInvestimento(1L);

        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<Investimento> lista = (List<Investimento>) response.getEntity();
        assertEquals(2, lista.size());
        assertEquals("CDB", lista.get(0).tipo());
        verify(useCase).consultar(1L);
    }

    @Test
    @DisplayName("GET /investimentos/{id} id inválido propaga erro negócio")
    void consultarInvestimentosIdInvalido() {
        NegocioException ex = new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "ID do cliente inválido");
        when(useCase.consultar(0L)).thenThrow(ex);

        NegocioException thrown = assertThrows(NegocioException.class, () -> resource.consultarInvestimento(0L));
        assertEquals("ID do cliente inválido", thrown.getMessage());
        verify(useCase).consultar(0L);
    }
}
