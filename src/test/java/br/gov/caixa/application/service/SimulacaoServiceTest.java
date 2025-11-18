package br.gov.caixa.application.service;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoResultado;
import br.gov.caixa.domain.port.out.ProdutoRepository;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class SimulacaoServiceTest {

    private SimulacaoService service;
    private ProdutoRepository produtoRepository;
    private SimulacaoRepository simulacaoRepository;

    @BeforeEach
    void setUp() {
        service = new SimulacaoService();
        produtoRepository = mock(ProdutoRepository.class);
        simulacaoRepository = mock(SimulacaoRepository.class);
        // campos têm escopo de pacote; teste está no mesmo package
        service.produtoRepository = produtoRepository;
        service.simulacaoRepository = simulacaoRepository;
    }

    @Test
    @DisplayName("Deve executar simulacao com sucesso e calcular valores esperados")
    void executarComSucesso() {
        // dado
        Produto produto = new Produto(10L, "CDB Premium", "CDB", new BigDecimal("0.12"), "BAIXO");
        when(produtoRepository.findByTipo("CDB")).thenReturn(Optional.of(produto));
        Simulacao simulacao = new Simulacao(1L, BigDecimal.valueOf(1000), 12, "CDB");

        // quando
        SimulacaoResultado resultado = service.executar(simulacao);

        // então: valida DTO resultado
        assertNotNull(resultado);
        assertEquals("CDB Premium", resultado.produtoValidado().nome());
        assertEquals(new BigDecimal("1126.83"), resultado.resultadoSimulacao().valorFinal());
        assertEquals(new BigDecimal("0.13"), resultado.resultadoSimulacao().rentabilidadeEfetiva());
        assertEquals(12, resultado.resultadoSimulacao().prazoMeses());
        assertEquals(LocalDate.now(), resultado.dataSimulacao());

        // verifica chamada ao repository com valor final não arredondado
        ArgumentCaptor<BigDecimal> valorFinalCaptor = ArgumentCaptor.forClass(BigDecimal.class);
        verify(simulacaoRepository).salvarSimulacao(eq(1L), eq("CDB Premium"), eq(BigDecimal.valueOf(1000)), valorFinalCaptor.capture(), eq(12), any(LocalDate.class));

        BigDecimal valorFinalBruto = valorFinalCaptor.getValue();
        BigDecimal esperado = BigDecimal.valueOf(1000).multiply(BigDecimal.ONE.add(new BigDecimal("0.12").divide(BigDecimal.valueOf(12), MathContext.DECIMAL64), MathContext.DECIMAL64).pow(12, MathContext.DECIMAL64), MathContext.DECIMAL64);
        assertEquals(0, valorFinalBruto.compareTo(esperado));
    }

    @Test
    @DisplayName("Deve lançar exceção quando produto não encontrado")
    void executarProdutoNaoEncontrado() {
        when(produtoRepository.findByTipo("CDB")).thenReturn(Optional.empty());
        Simulacao simulacao = new Simulacao(1L, BigDecimal.valueOf(1000), 6, "CDB");

        NegocioException ex = assertThrows(NegocioException.class, () -> service.executar(simulacao));
        assertEquals(404, ex.getCodigo());
        assertEquals("Produto não encontrado", ex.getMessage());
        verify(simulacaoRepository, never()).salvarSimulacao(any(), any(), any(), any(), any(), any());
    }

    @Test
    @DisplayName("Valor deve ser maior que zero")
    void validarValorZero() {
        Simulacao simulacao = new Simulacao(1L, BigDecimal.ZERO, 6, "CDB");
        NegocioException ex = assertThrows(NegocioException.class, () -> service.executar(simulacao));
        assertEquals(400, ex.getCodigo());
        assertTrue(ex.getMessage().contains("Valor"));
    }

    @Test
    @DisplayName("Prazo deve ser maior que zero")
    void validarPrazoZero() {
        Simulacao simulacao = new Simulacao(1L, BigDecimal.valueOf(1000), 0, "CDB");
        NegocioException ex = assertThrows(NegocioException.class, () -> service.executar(simulacao));
        assertEquals(400, ex.getCodigo());
        assertTrue(ex.getMessage().contains("Prazo"));
    }

    @Test
    @DisplayName("Tipo de produto inválido")
    void validarTipoProdutoInvalido() {
        Simulacao simulacao = new Simulacao(1L, BigDecimal.valueOf(1000), 6, "");
        NegocioException ex = assertThrows(NegocioException.class, () -> service.executar(simulacao));
        assertEquals(400, ex.getCodigo());
        assertTrue(ex.getMessage().contains("Tipo"));
    }

    @Test
    @DisplayName("ClienteId inválido")
    void validarClienteIdInvalido() {
        Simulacao simulacao = new Simulacao(0L, BigDecimal.valueOf(1000), 6, "CDB");
        NegocioException ex = assertThrows(NegocioException.class, () -> service.executar(simulacao));
        assertEquals(400, ex.getCodigo());
        assertTrue(ex.getMessage().contains("cliente"));
    }
}
