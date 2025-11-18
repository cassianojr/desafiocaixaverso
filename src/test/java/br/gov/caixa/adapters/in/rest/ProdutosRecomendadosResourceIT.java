package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.out.ProdutoRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class ProdutosRecomendadosResourceIT {

    @Inject
    EntityManager em;

    @Inject
    ProdutoRepository produtoRepository;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM ProdutoEntity").executeUpdate();
        em.flush();
        // inserir produtos de diferentes riscos
        inserirProduto(1L, "CDB Seguro", "CDB", BigDecimal.valueOf(0.12), "BAIXO");
        inserirProduto(2L, "LCI Imobiliária", "LCI", BigDecimal.valueOf(0.10), "BAIXO");
        inserirProduto(3L, "Ações Tech", "ACOES", BigDecimal.valueOf(0.30), "ALTO");
        inserirProduto(4L, "Fundo Moderado", "FUNDOS", BigDecimal.valueOf(0.18), "MEDIO");
    }

    @Transactional
    void inserirProduto(Long id, String nome, String tipo, BigDecimal rentabilidade, String risco) {
        em.createNativeQuery("INSERT INTO PRODUTOS (ID, NOME, TIPO, RENTABILIDADE, RISCO) VALUES (?,?,?,?,?)")
                .setParameter(1, id)
                .setParameter(2, nome)
                .setParameter(3, tipo)
                .setParameter(4, rentabilidade)
                .setParameter(5, risco)
                .executeUpdate();
    }

    @Test
    @DisplayName("GET /produtos-recomendados/CONSERVADOR retorna apenas risco BAIXO")
    @TestSecurity(user = "admin", roles = {"admin"})
    void recomendarConservador() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/CONSERVADOR")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("risco", everyItem(equalTo("BAIXO")))
            .body("tipo", hasItems("CDB", "LCI"));
    }

    @Test
    @DisplayName("GET /produtos-recomendados/MODERADO retorna risco MEDIO")
    @TestSecurity(user = "admin", roles = {"admin"})
    void recomendarModerado() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/MODERADO")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].risco", equalTo("MEDIO"))
            .body("[0].tipo", equalTo("FUNDOS"));
    }

    @Test
    @DisplayName("GET /produtos-recomendados/AGRESSIVO retorna risco ALTO")
    @TestSecurity(user = "admin", roles = {"admin"})
    void recomendarAgressivo() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/AGRESSIVO")
        .then()
            .statusCode(200)
            .body("size()", equalTo(1))
            .body("[0].risco", equalTo("ALTO"))
            .body("[0].tipo", equalTo("ACOES"));
    }

    @Test
    @DisplayName("GET /produtos-recomendados/invalido retorna 400")
    @TestSecurity(user = "admin", roles = {"admin"})
    void recomendarInvalido() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/produtos-recomendados/INVALIDO")
        .then()
            .statusCode(400);
    }
}
