package br.gov.caixa.adapters.out.persistence.entity;

import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilFaixaFrequenciaEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilFaixaVolumeEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilInvestidorFaixaPontuacaoEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilPreferenciaLiquidezEntity;
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

    @Test
    @DisplayName("PerfilFaixaVolumeEntity getters/setters")
    void perfilFaixaVolumeEntity() {
        PerfilFaixaVolumeEntity e = new PerfilFaixaVolumeEntity();
        e.setId(1L);
        e.setValorMin(BigDecimal.valueOf(0));
        e.setValorMax(BigDecimal.valueOf(4999.99));
        e.setPontos(10);
        e.setDescricao("Volume menor que 5.000");

        assertEquals(1L, e.getId());
        assertEquals(0, e.getValorMin().compareTo(BigDecimal.valueOf(0)));
        assertEquals(0, e.getValorMax().compareTo(BigDecimal.valueOf(4999.99)));
        assertEquals(10, e.getPontos());
        assertEquals("Volume menor que 5.000", e.getDescricao());
    }

    @Test
    @DisplayName("PerfilFaixaFrequenciaEntity getters/setters")
    void perfilFaixaFrequenciaEntity() {
        PerfilFaixaFrequenciaEntity e = new PerfilFaixaFrequenciaEntity();
        e.setId(1L);
        e.setQtdMin(0);
        e.setQtdMax(1);
        e.setPontos(10);
        e.setDescricao("Menos de 2 investimentos");

        assertEquals(1L, e.getId());
        assertEquals(0, e.getQtdMin());
        assertEquals(1, e.getQtdMax());
        assertEquals(10, e.getPontos());
        assertEquals("Menos de 2 investimentos", e.getDescricao());
    }

    @Test
    @DisplayName("PerfilPreferenciaLiquidezEntity getters/setters")
    void perfilPreferenciaLiquidezEntity() {
        PerfilPreferenciaLiquidezEntity e = new PerfilPreferenciaLiquidezEntity();
        e.setId(1L);
        e.setPercentualMin(BigDecimal.valueOf(50.00));
        e.setPercentualMax(null);
        e.setPontos(10);
        e.setDescricao("Preferência alta por alta liquidez");

        assertEquals(1L, e.getId());
        assertEquals(0, e.getPercentualMin().compareTo(BigDecimal.valueOf(50.00)));
        assertNull(e.getPercentualMax());
        assertEquals(10, e.getPontos());
        assertEquals("Preferência alta por alta liquidez", e.getDescricao());
    }

    @Test
    @DisplayName("PerfilInvestidorFaixaPontuacaoEntity getters/setters")
    void perfilInvestidorFaixaPontuacaoEntity() {
        PerfilInvestidorFaixaPontuacaoEntity e = new PerfilInvestidorFaixaPontuacaoEntity();
        e.setId(1L);
        e.setPontosMin(0);
        e.setPontosMax(40);
        e.setPerfil("CONSERVADOR");
        e.setDescricao("Busca segurança e baixa variação, priorizando liquidez.");

        assertEquals(1L, e.getId());
        assertEquals(0, e.getPontosMin());
        assertEquals(40, e.getPontosMax());
        assertEquals("CONSERVADOR", e.getPerfil());
        assertEquals("Busca segurança e baixa variação, priorizando liquidez.", e.getDescricao());
    }
}
