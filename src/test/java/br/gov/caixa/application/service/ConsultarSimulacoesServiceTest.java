package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ConsultarSimulacoesServiceTest {

    private ConsultarSimulacoesService service;
    private SimulacaoRepository simulacaoRepository;

    @BeforeEach
    void setUp() {
        service = new ConsultarSimulacoesService();
        simulacaoRepository = mock(SimulacaoRepository.class);
        service.simulacaoRepository = simulacaoRepository; // mesmo package para injeção
    }

    private SimulacaoHistorico hist(long id, long cliente, String produto, double valorInvestido, double valorFinal, int prazo, LocalDate data) {
        return new SimulacaoHistorico(id, cliente, produto, BigDecimal.valueOf(valorInvestido), BigDecimal.valueOf(valorFinal), prazo, data);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando repositório não possuir registros")
    void listarTodasVazio() {
        when(simulacaoRepository.listarTodas()).thenReturn(List.of());
        List<SimulacaoHistorico> lista = service.listarTodas();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
        verify(simulacaoRepository).listarTodas();
    }

    @Test
    @DisplayName("Deve retornar lista com historicos preservando ordem e dados")
    void listarTodasComDados() {
        LocalDate hoje = LocalDate.now();
        List<SimulacaoHistorico> mockLista = List.of(
                hist(1, 10, "CDB Premium", 1000, 1126.83, 12, hoje.minusDays(2)),
                hist(2, 11, "LCI", 5000, 5255.50, 6, hoje.minusDays(1)),
                hist(3, 12, "ACOES", 2000, 2500.00, 18, hoje)
        );
        when(simulacaoRepository.listarTodas()).thenReturn(mockLista);

        List<SimulacaoHistorico> lista = service.listarTodas();

        assertEquals(3, lista.size());
        assertEquals(mockLista, lista); // compara ordem e conteúdo
        assertEquals("CDB Premium", lista.get(0).produto());
        assertEquals(BigDecimal.valueOf(5255.50), lista.get(1).valorFinal());
        assertEquals(18, lista.get(2).prazoMeses());
        verify(simulacaoRepository).listarTodas();
    }
}
