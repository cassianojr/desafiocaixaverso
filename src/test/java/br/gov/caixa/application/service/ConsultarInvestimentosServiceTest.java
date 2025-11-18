package br.gov.caixa.application.service;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
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
class ConsultarInvestimentosServiceTest {

    private ConsultarInvestimentosService service;
    private InvestimentoRepository investimentoRepository;

    @BeforeEach
    void setUp() {
        service = new ConsultarInvestimentosService();
        investimentoRepository = mock(InvestimentoRepository.class);
        service.investimentoRepository = investimentoRepository; // mesmo package
    }

    private Investimento inv(long id, long cliente, String tipo, double valor, double rent) {
        return new Investimento(id, cliente, tipo, BigDecimal.valueOf(valor), BigDecimal.valueOf(rent), LocalDate.now());
    }

    @Test
    @DisplayName("Deve lançar NegocioException para clienteId inválido")
    void clienteIdInvalido() {
        NegocioException ex1 = assertThrows(NegocioException.class, () -> service.consultar(null));
        NegocioException ex2 = assertThrows(NegocioException.class, () -> service.consultar(0L));
        assertEquals(400, ex1.getCodigo());
        assertEquals(400, ex2.getCodigo());
        verifyNoInteractions(investimentoRepository);
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver investimentos")
    void listaVazia() {
        when(investimentoRepository.listarPorCliente(10L)).thenReturn(List.of());
        List<Investimento> lista = service.consultar(10L);
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
        verify(investimentoRepository).listarPorCliente(10L);
    }

    @Test
    @DisplayName("Deve retornar lista com investimentos do cliente")
    void listaComDados() {
        List<Investimento> mockLista = List.of(
                inv(1, 11, "CDB", 3000.0, 0.12),
                inv(2, 11, "LCI", 5000.0, 0.10)
        );
        when(investimentoRepository.listarPorCliente(11L)).thenReturn(mockLista);

        List<Investimento> lista = service.consultar(11L);

        assertEquals(2, lista.size());
        assertEquals(mockLista, lista);
        assertEquals("CDB", lista.get(0).tipo());
        assertEquals(BigDecimal.valueOf(5000.0), lista.get(1).valor());
        verify(investimentoRepository).listarPorCliente(11L);
    }
}
