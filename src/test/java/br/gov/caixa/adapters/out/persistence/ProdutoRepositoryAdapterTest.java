package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.ProdutoEntity;
import br.gov.caixa.domain.model.Produto;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ProdutoRepositoryAdapterTest {

    @Inject
    ProdutoRepositoryAdapter adapter;

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM ProdutoEntity").executeUpdate();
        em.flush();
    }

    @Transactional
    void persist(String nome, String tipo, double rent, String risco) {
        ProdutoEntity e = new ProdutoEntity();
        e.setNome(nome);
        e.setTipo(tipo);
        e.setRentabilidade(BigDecimal.valueOf(rent));
        e.setRisco(risco);
        em.persist(e);
    }

    @Test
    @DisplayName("findAll deve retornar todos os produtos mapeados")
    void findAll() {
        persist("CDB Premium","CDB",0.12,"BAIXO");
        persist("LCI Garantida","LCI",0.10,"BAIXO");
        persist("FII Log","FII",0.15,"MEDIO");

        List<Produto> todos = adapter.findAll();
        assertEquals(3, todos.size());
        assertTrue(todos.stream().anyMatch(p -> p.nome().equals("CDB Premium")));
        assertTrue(todos.stream().anyMatch(p -> p.tipo().equals("FII")));
    }

    @Test
    @DisplayName("findByTipo deve retornar Optional vazio quando não existir")
    void findByTipoInexistente() {
        persist("CDB Premium","CDB",0.12,"BAIXO");
        Optional<Produto> p = adapter.findByTipo("LCI");
        assertTrue(p.isEmpty());
    }

    @Test
    @DisplayName("findByTipo deve retornar produto correto quando existir")
    void findByTipoExistente() {
        persist("CDB Premium","CDB",0.12,"BAIXO");
        Optional<Produto> p = adapter.findByTipo("CDB");
        assertTrue(p.isPresent());
        assertEquals("CDB Premium", p.get().nome());
        assertEquals("BAIXO", p.get().risco());
    }

    @Test
    @DisplayName("findByRisco deve retornar lista filtrada pelo risco")
    void findByRisco() {
        persist("CDB Premium","CDB",0.12,"BAIXO");
        persist("LCI Garantida","LCI",0.10,"BAIXO");
        persist("FII Log","FII",0.15,"MEDIO");
        persist("Acoes Tech","ACOES",0.25,"ALTO");

        List<Produto> baixo = adapter.findByRisco("BAIXO");
        assertEquals(2, baixo.size());
        assertTrue(baixo.stream().allMatch(p -> p.risco().equals("BAIXO")));

        List<Produto> medio = adapter.findByRisco("MEDIO");
        assertEquals(1, medio.size());
        assertEquals("FII Log", medio.getFirst().nome());

        List<Produto> alto = adapter.findByRisco("ALTO");
        assertEquals(1, alto.size());
        assertEquals("Acoes Tech", alto.getFirst().nome());
    }

    @Test
    @DisplayName("findByRisco para risco inexistente deve retornar lista vazia")
    void findByRiscoInexistente() {
        persist("CDB Premium","CDB",0.12,"BAIXO");
        List<Produto> alto = adapter.findByRisco("ALTO");
        assertTrue(alto.isEmpty());
    }
}
