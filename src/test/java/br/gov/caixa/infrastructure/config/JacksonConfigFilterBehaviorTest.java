package br.gov.caixa.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class JacksonConfigFilterBehaviorTest {

    @Inject
    ObjectMapper objectMapper;

    @Inject
    EntityManager em;

    Long produtoId;

    @BeforeEach
    @Transactional
    void setup() {
        // Limpa registros anteriores com o mesmo nome para evitar múltiplos resultados
        em.createNativeQuery("DELETE FROM produto WHERE nome = ?")
            .setParameter(1, "CDB Test")
            .executeUpdate();

        em.createNativeQuery("INSERT INTO produto (nome, tipo, rentabilidade, risco) VALUES (?,?,?,?)")
            .setParameter(1, "CDB Test")
            .setParameter(2, "CDB")
            .setParameter(3, BigDecimal.valueOf(0.12))
            .setParameter(4, "BAIXO")
            .executeUpdate();
        em.flush();
        // Recupera o último id inserido para o nome específico
        produtoId = ((Number) em.createNativeQuery("SELECT id FROM produto WHERE nome = ? ORDER BY id DESC")
            .setParameter(1, "CDB Test")
            .getResultList()
            .get(0)).longValue();
    }

    @Test
    @DisplayName("Proxy não inicializado serializa apenas ID (propriedade inicializada)")
    @Transactional
    void proxyNaoInicializadoIgnorado() throws Exception {
        Object proxy = em.getReference(br.gov.caixa.adapters.out.persistence.entity.ProdutoEntity.class, produtoId);
        assertFalse(Hibernate.isInitialized(proxy));

        String json = objectMapper.writeValueAsString(proxy);
        var tree = objectMapper.readTree(json);
        assertEquals(1, tree.size());
        assertTrue(tree.has("id"));
        assertEquals(produtoId.longValue(), tree.get("id").asLong());
    }

    @Test
    @DisplayName("Após inicialização do proxy campos são serializados")
    @Transactional
    void proxyInicializadoSerializaCampos() throws Exception {
        br.gov.caixa.adapters.out.persistence.entity.ProdutoEntity proxy = em.getReference(br.gov.caixa.adapters.out.persistence.entity.ProdutoEntity.class, produtoId);
        // Força inicialização acessando um campo
        String nome = proxy.getNome();
        assertTrue(Hibernate.isInitialized(proxy));
        assertEquals("CDB Test", nome);

        String json = objectMapper.writeValueAsString(proxy);
        // Deve conter campo nome e possivelmente outros
        assertTrue(json.contains("\"nome\""));
        assertTrue(json.contains("CDB Test"));
    }
}
