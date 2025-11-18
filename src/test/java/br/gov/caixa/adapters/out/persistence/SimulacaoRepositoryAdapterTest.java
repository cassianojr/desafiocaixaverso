package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.SimulacaoEntity;
import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import jakarta.transaction.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class SimulacaoRepositoryAdapterTest {

    @Inject
    SimulacaoRepositoryAdapter adapter;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void limparTabela() {
        em.createQuery("DELETE FROM SimulacaoEntity").executeUpdate();
        em.flush();
    }

    @Test
    @DisplayName("salvarSimulacao deve persistir entidade com campos corretos")
    void salvarSimulacao() {
        LocalDate hoje = LocalDate.now();
        adapter.salvarSimulacao(10L, "CDB Premium", BigDecimal.valueOf(1000), BigDecimal.valueOf(1126.83), 12, hoje);

        List<SimulacaoEntity> list = em.createQuery("FROM SimulacaoEntity WHERE clienteId = :c", SimulacaoEntity.class)
                .setParameter("c", 10L)
                .getResultList();
        assertEquals(1, list.size());
        SimulacaoEntity e = list.getFirst();
        assertEquals("CDB Premium", e.getProduto());
        // comparação ignorando escala
        assertEquals(0, e.getValorInvestido().compareTo(BigDecimal.valueOf(1000)));
        assertEquals(BigDecimal.valueOf(1126.83), e.getValorFinal());
        assertEquals(12, e.getPrazoMeses());
        assertEquals(hoje, e.getDataSimulacao());
    }

    @Test
    @DisplayName("listarTodas deve converter entidades para dominio")
    void listarTodas() {
        LocalDate hoje = LocalDate.now();
        // seed
        adapter.salvarSimulacao(1L, "CDB", BigDecimal.valueOf(500), BigDecimal.valueOf(520), 6, hoje);
        adapter.salvarSimulacao(2L, "LCI", BigDecimal.valueOf(1000), BigDecimal.valueOf(1050), 12, hoje);

        List<SimulacaoHistorico> historico = adapter.listarTodas();
        assertEquals(2, historico.size());
        assertTrue(historico.stream().anyMatch(h -> h.produto().equals("CDB")));
        assertTrue(historico.stream().anyMatch(h -> h.valorFinal().compareTo(BigDecimal.valueOf(1050)) == 0));
    }

    @Test
    @DisplayName("agruparPorProdutoEDia deve agrupar quantidade e média por produto/dia")
    void agruparPorProdutoEDia() {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        // CDB hoje 2 registros
        adapter.salvarSimulacao(3L, "CDB", BigDecimal.valueOf(1000), BigDecimal.valueOf(1100), 6, hoje);
        adapter.salvarSimulacao(4L, "CDB", BigDecimal.valueOf(2000), BigDecimal.valueOf(2200), 6, hoje);
        // LCI hoje 1 registro
        adapter.salvarSimulacao(5L, "LCI", BigDecimal.valueOf(1500), BigDecimal.valueOf(1600), 12, hoje);
        // CDB ontem 1 registro
        adapter.salvarSimulacao(6L, "CDB", BigDecimal.valueOf(500), BigDecimal.valueOf(530), 3, ontem);

        List<SimulacaoPorProdutoDia> agrupado = adapter.agruparPorProdutoEDia();

        // Grupos distintos por (produto,dia): CDB-hoje, CDB-ontem, LCI-hoje -> total 3 grupos
        assertEquals(3, agrupado.size());

        SimulacaoPorProdutoDia cdbHoje = agrupado.stream().filter(a -> a.produto().equals("CDB") && a.data().equals(hoje)).findFirst().orElseThrow();
        assertEquals(2L, cdbHoje.quantidadeSimulacoes());
        double mediaEsperadaCdbHoje = (1100.0 + 2200.0) / 2.0;
        assertEquals(mediaEsperadaCdbHoje, cdbHoje.mediaValorFinal(), 0.0001);

        SimulacaoPorProdutoDia cdbOntem = agrupado.stream().filter(a -> a.produto().equals("CDB") && a.data().equals(ontem)).findFirst().orElseThrow();
        assertEquals(1L, cdbOntem.quantidadeSimulacoes());
        assertEquals(530.0, cdbOntem.mediaValorFinal(), 0.0001);

        SimulacaoPorProdutoDia lciHoje = agrupado.stream().filter(a -> a.produto().equals("LCI") && a.data().equals(hoje)).findFirst().orElseThrow();
        assertEquals(1L, lciHoje.quantidadeSimulacoes());
        assertEquals(1600.0, lciHoje.mediaValorFinal(), 0.0001);
    }
}
