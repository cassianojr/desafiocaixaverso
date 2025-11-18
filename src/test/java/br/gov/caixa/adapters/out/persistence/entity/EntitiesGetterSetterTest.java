package br.gov.caixa.adapters.out.persistence.entity;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class EntitiesGetterSetterTest {

    @Test
    @DisplayName("ProdutoEntity getters/setters")
    void produtoEntity() {
        ProdutoEntity e = new ProdutoEntity();
        e.setId(1L);
        e.setNome("CDB Premium");
        e.setTipo("CDB");
        e.setRentabilidade(BigDecimal.valueOf(0.12));
        e.setRisco("BAIXO");

        assertEquals(1L, e.getId());
        assertEquals("CDB Premium", e.getNome());
        assertEquals("CDB", e.getTipo());
        assertEquals(0, e.getRentabilidade().compareTo(BigDecimal.valueOf(0.12)));
        assertEquals("BAIXO", e.getRisco());
    }

    @Test
    @DisplayName("InvestimentoEntity getters/setters")
    void investimentoEntity() {
        InvestimentoEntity e = new InvestimentoEntity();
        LocalDate data = LocalDate.now();
        e.setId(2L);
        e.setClienteId(10L);
        e.setTipo("LCI");
        e.setValor(BigDecimal.valueOf(5000));
        e.setRentabilidade(BigDecimal.valueOf(0.10));
        e.setData(data);

        assertEquals(2L, e.getId());
        assertEquals(10L, e.getClienteId());
        assertEquals("LCI", e.getTipo());
        assertEquals(0, e.getValor().compareTo(BigDecimal.valueOf(5000)));
        assertEquals(0, e.getRentabilidade().compareTo(BigDecimal.valueOf(0.10)));
        assertEquals(data, e.getData());
    }

    @Test
    @DisplayName("SimulacaoEntity getters/setters")
    void simulacaoEntity() {
        SimulacaoEntity e = new SimulacaoEntity();
        LocalDate data = LocalDate.now().minusDays(1);
        e.setId(3L);
        e.setClienteId(20L);
        e.setProduto("FII Log");
        e.setValorInvestido(BigDecimal.valueOf(2000));
        e.setValorFinal(BigDecimal.valueOf(2100));
        e.setPrazoMeses(12);
        e.setDataSimulacao(data);

        assertEquals(3L, e.getId());
        assertEquals(20L, e.getClienteId());
        assertEquals("FII Log", e.getProduto());
        assertEquals(0, e.getValorInvestido().compareTo(BigDecimal.valueOf(2000)));
        assertEquals(0, e.getValorFinal().compareTo(BigDecimal.valueOf(2100)));
        assertEquals(12, e.getPrazoMeses());
        assertEquals(data, e.getDataSimulacao());
    }

    @Test
    @DisplayName("TelemetriaEntity getters/setters")
    void telemetriaEntity() {
        TelemetriaEntity e = new TelemetriaEntity();
        LocalDate data = LocalDate.now();
        e.setId(4L);
        e.setServico("/simulacoes");
        e.setQuantidadeChamadas(5L);
        e.setMediaTempoRespostaMs(123.45);
        e.setData(data);

        assertEquals(4L, e.getId());
        assertEquals("/simulacoes", e.getServico());
        assertEquals(5L, e.getQuantidadeChamadas());
        assertEquals(123.45, e.getMediaTempoRespostaMs());
        assertEquals(data, e.getData());
    }
}
