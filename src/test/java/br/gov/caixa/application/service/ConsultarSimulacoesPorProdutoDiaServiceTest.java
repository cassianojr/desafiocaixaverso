package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ConsultarSimulacoesPorProdutoDiaServiceTest {

    private ConsultarSimulacoesPorProdutoDiaService service;
    private SimulacaoRepository simulacaoRepository;

    @BeforeEach
    void setUp() {
        service = new ConsultarSimulacoesPorProdutoDiaService();
        simulacaoRepository = mock(SimulacaoRepository.class);
        service.simulacaoRepository = simulacaoRepository; // mesmo package
    }

    private SimulacaoPorProdutoDia reg(String produto, LocalDate data, long qtd, double media) {
        return new SimulacaoPorProdutoDia(produto, data, qtd, media);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver agrupamentos")
    void consultarVazio() {
        when(simulacaoRepository.agruparPorProdutoEDia()).thenReturn(List.of());
        List<SimulacaoPorProdutoDia> lista = service.consultar();
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
        verify(simulacaoRepository).agruparPorProdutoEDia();
    }

    @Test
    @DisplayName("Deve retornar agrupamentos por produto e dia preservando ordem mock")
    void consultarComDados() {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        List<SimulacaoPorProdutoDia> mockLista = List.of(
                reg("CDB Premium", ontem, 5, 1130.25),
                reg("LCI", hoje, 3, 5300.10),
                reg("ACOES", hoje, 7, 2550.00)
        );
        when(simulacaoRepository.agruparPorProdutoEDia()).thenReturn(mockLista);

        List<SimulacaoPorProdutoDia> lista = service.consultar();

        assertEquals(3, lista.size());
        assertEquals(mockLista, lista); // ordem e conteúdo
        assertEquals("CDB Premium", lista.get(0).produto());
        assertEquals(7L, lista.get(2).quantidadeSimulacoes());
        assertEquals(5300.10, lista.get(1).mediaValorFinal());
        verify(simulacaoRepository).agruparPorProdutoEDia();
    }

    @Test
    @DisplayName("Verifica múltiplos dias para mesmo produto")
    void consultarMesmoProdutoDiasDiferentes() {
        LocalDate hoje = LocalDate.now();
        LocalDate diaAnterior = hoje.minusDays(2);
        List<SimulacaoPorProdutoDia> mockLista = List.of(
                reg("CDB Premium", diaAnterior, 2, 1105.00),
                reg("CDB Premium", hoje, 4, 1120.75)
        );
        when(simulacaoRepository.agruparPorProdutoEDia()).thenReturn(mockLista);

        List<SimulacaoPorProdutoDia> lista = service.consultar();
        assertEquals(2, lista.size());
        assertEquals("CDB Premium", lista.get(0).produto());
        assertEquals("CDB Premium", lista.get(1).produto());
        assertTrue(lista.get(1).mediaValorFinal() > lista.get(0).mediaValorFinal());
        verify(simulacaoRepository).agruparPorProdutoEDia();
    }
}
