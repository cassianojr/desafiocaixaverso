package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.model.ResultadoSimulacao;
import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoResultado;
import br.gov.caixa.domain.port.in.SimularInvestimentoUseCase;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class SimulacaoResourceTest {

    SimularInvestimentoUseCase useCase;
    SimulacaoResource resource;

    @BeforeEach
    void setup() {
        useCase = mock(SimularInvestimentoUseCase.class);
        resource = new SimulacaoResource();
        resource.simularInvestimento = useCase; // injetar mock
    }

    @Test
    @DisplayName("POST /simular-investimento retorna 200 e corpo resultado")
    void simularInvestimentoOk() {
        Simulacao input = new Simulacao(1L, BigDecimal.valueOf(1000), 12, "CDB");
        Produto produto = new Produto(10L, "CDB XPTO", "CDB", BigDecimal.valueOf(0.12), "BAIXO");
        ResultadoSimulacao resultado = new ResultadoSimulacao(BigDecimal.valueOf(1120), BigDecimal.valueOf(0.12), 12);
        SimulacaoResultado simulacaoResultado = new SimulacaoResultado(produto, resultado, LocalDate.now());
        when(useCase.executar(input)).thenReturn(simulacaoResultado);

        var response = resource.simularInvestimento(input);

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof SimulacaoResultado);
        SimulacaoResultado entity = (SimulacaoResultado) response.getEntity();
        assertEquals("CDB XPTO", entity.produtoValidado().nome());
        verify(useCase).executar(input);
    }

    @Test
    @DisplayName("POST /simular-investimento retorna erro de negócio mapeado")
    void simularInvestimentoErroNegocio() {
        Simulacao input = new Simulacao(0L, BigDecimal.ZERO, 0, "");
        NegocioException ex = new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "dados inválidos");
        when(useCase.executar(input)).thenThrow(ex);

        NegocioException thrown = assertThrows(NegocioException.class, () -> resource.simularInvestimento(input));
        assertEquals("dados inválidos", thrown.getMessage());
        verify(useCase).executar(input);
    }
}
