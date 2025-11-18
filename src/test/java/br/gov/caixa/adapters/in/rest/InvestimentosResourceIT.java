package br.gov.caixa.adapters.in.rest;

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
import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class InvestimentosResourceIT {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM InvestimentoEntity").executeUpdate();
        em.flush();
    }

    @Transactional
    void inserirInvestimento(Long id, Long clienteId, String tipo, BigDecimal valor, BigDecimal rentabilidade, LocalDate data) {
        em.createNativeQuery("INSERT INTO INVESTIMENTOS (ID, CLIENTE_ID, TIPO, VALOR, RENTABILIDADE, DATA) VALUES (?,?,?,?,?,?)")
                .setParameter(1, id)
                .setParameter(2, clienteId)
                .setParameter(3, tipo)
                .setParameter(4, valor)
                .setParameter(5, rentabilidade)
                .setParameter(6, data)
                .executeUpdate();
    }

    @Test
    @DisplayName("GET /investimentos/{id} vazio retorna lista vazia")
    @TestSecurity(user = "admin", roles = {"admin"})
    void consultarVazio() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/investimentos/999")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    @DisplayName("GET /investimentos/{id} retorna lista de investimentos do cliente")
    @TestSecurity(user = "admin", roles = {"admin"})
    void consultarComDados() {
        inserirInvestimento(1L, 10L, "CDB", BigDecimal.valueOf(1000), BigDecimal.valueOf(0.12), LocalDate.now().minusDays(2));
        inserirInvestimento(2L, 10L, "ACOES", BigDecimal.valueOf(2000), BigDecimal.valueOf(0.30), LocalDate.now().minusDays(1));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/investimentos/10")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("tipo", hasItems("CDB", "ACOES"))
            .body("find { it.tipo == 'CDB' }.valor", equalTo(1000))
            .body("find { it.tipo == 'ACOES' }.rentabilidade", equalTo(0.30F));
    }

    @Test
    @DisplayName("GET /investimentos/{id} id inválido retorna 400")
    @TestSecurity(user = "admin", roles = {"admin"})
    void consultarIdInvalido() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/investimentos/0")
        .then()
            .statusCode(400);
    }
}
