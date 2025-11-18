package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.InvestimentoEntity;
import br.gov.caixa.domain.model.Investimento;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class InvestimentoRepositoryAdapterTest {

    @Inject
    InvestimentoRepositoryAdapter adapter;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM InvestimentoEntity").executeUpdate();
        em.flush();
    }

    @Transactional
    void persist(long clienteId, String tipo, double valor, double rent, LocalDate data) {
        InvestimentoEntity e = new InvestimentoEntity();
        e.setClienteId(clienteId);
        e.setTipo(tipo);
        e.setValor(BigDecimal.valueOf(valor));
        e.setRentabilidade(BigDecimal.valueOf(rent));
        e.setData(data);
        em.persist(e);
    }

    @Test
    @DisplayName("listarPorCliente deve retornar lista vazia quando não há investimentos")
    void listarPorClienteVazio() {
        List<Investimento> lista = adapter.listarPorCliente(99L);
        assertNotNull(lista);
        assertTrue(lista.isEmpty());
    }

    @Test
    @DisplayName("listarPorCliente deve retornar somente investimentos do cliente solicitado")
    void listarPorClienteComDados() {
        LocalDate hoje = LocalDate.now();
        persist(10L, "CDB", 1000.0, 0.12, hoje);
        persist(10L, "LCI", 5000.0, 0.10, hoje.minusDays(1));
        persist(11L, "FII", 2000.0, 0.15, hoje); // outro cliente

        List<Investimento> lista = adapter.listarPorCliente(10L);

        assertEquals(2, lista.size());
        assertTrue(lista.stream().allMatch(i -> i.clienteId().equals(10L)));
        assertTrue(lista.stream().anyMatch(i -> i.tipo().equals("CDB")));
        assertTrue(lista.stream().anyMatch(i -> i.tipo().equals("LCI")));
    }

    @Test
    @DisplayName("listarPorCliente deve mapear corretamente campos de Investimento")
    void listarPorClienteMapeamentoCampos() {
        LocalDate data = LocalDate.now().minusDays(2);
        persist(20L, "ACOES", 3000.0, 0.25, data);

        List<Investimento> lista = adapter.listarPorCliente(20L);
        assertEquals(1, lista.size());
        Investimento inv = lista.getFirst();
        assertEquals(20L, inv.clienteId());
        assertEquals("ACOES", inv.tipo());
        assertEquals(0, inv.valor().compareTo(BigDecimal.valueOf(3000.0)));
        assertEquals(0, inv.rentabilidade().compareTo(BigDecimal.valueOf(0.25)));
        assertEquals(data, inv.data());
    }
}
