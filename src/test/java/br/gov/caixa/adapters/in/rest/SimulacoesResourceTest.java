package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesPorProdutoDiaUseCase;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesUseCase;
import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Testes unitários do resource SimulacoesResource validando o mapeamento de respostas.
 */
@QuarkusTest
class SimulacoesResourceTest {

    ConsultarSimulacoesUseCase consultarSimulacoesUseCase;
    ConsultarSimulacoesPorProdutoDiaUseCase consultarSimulacoesPorProdutoDiaUseCase;
    SimulacoesResource resource;

    @BeforeEach
    void setup() {
        consultarSimulacoesUseCase = mock(ConsultarSimulacoesUseCase.class);
        consultarSimulacoesPorProdutoDiaUseCase = mock(ConsultarSimulacoesPorProdutoDiaUseCase.class);
        resource = new SimulacoesResource();
        // Acesso pacote-padrão permite atribuição direta
        resource.consultarSimulacoesUse = consultarSimulacoesUseCase;
        resource.consultarSimulacoesPorProdutoDia = consultarSimulacoesPorProdutoDiaUseCase;
    }

    @Test
    @DisplayName("listarSimulacoes deve retornar 200 e lista vazia quando não há dados")
    void listarSimulacoesVazio() {
        when(consultarSimulacoesUseCase.listarTodas()).thenReturn(List.of());

        var response = resource.listarSimulacoes();

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof List<?>);
        assertEquals(0, ((List<?>) response.getEntity()).size());
        verify(consultarSimulacoesUseCase).listarTodas();
    }

    @Test
    @DisplayName("listarSimulacoes deve retornar lista com simulacoes e status 200")
    void listarSimulacoesComDados() {
        var s1 = new SimulacaoHistorico(1L, 1L, "CDB", BigDecimal.valueOf(1000), BigDecimal.valueOf(1120), 12, LocalDate.now());
        var s2 = new SimulacaoHistorico(2L, 2L, "LCI", BigDecimal.valueOf(5000), BigDecimal.valueOf(5600), 24, LocalDate.now());
        when(consultarSimulacoesUseCase.listarTodas()).thenReturn(List.of(s1, s2));

        var response = resource.listarSimulacoes();

        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<SimulacaoHistorico> lista = (List<SimulacaoHistorico>) response.getEntity();
        assertEquals(2, lista.size());
        assertEquals("CDB", lista.get(0).produto());
        assertEquals(24, lista.get(1).prazoMeses());
        verify(consultarSimulacoesUseCase).listarTodas();
    }

    @Test
    @DisplayName("consultarSimulacoes por produto/dia deve retornar agregados e status 200")
    void consultarSimulacoesPorProdutoDia() {
        var sr = new SimulacaoPorProdutoDia("CDB", LocalDate.now(), 3L, 1200.0);
        when(consultarSimulacoesPorProdutoDiaUseCase.consultar()).thenReturn(List.of(sr));

        var response = resource.consultarSimulacoes();

        assertEquals(200, response.getStatus());
        @SuppressWarnings("unchecked")
        List<SimulacaoPorProdutoDia> lista = (List<SimulacaoPorProdutoDia>) response.getEntity();
        assertEquals(1, lista.size());
        assertEquals("CDB", lista.getFirst().produto());
        assertEquals(3L, lista.getFirst().quantidadeSimulacoes());
        verify(consultarSimulacoesPorProdutoDiaUseCase).consultar();
    }
}
