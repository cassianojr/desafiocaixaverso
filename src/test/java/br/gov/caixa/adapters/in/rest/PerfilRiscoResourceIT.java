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
class PerfilRiscoResourceIT {

    @Inject
    EntityManager em;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM InvestimentoEntity").executeUpdate();
        em.flush();
    }

    @Transactional
    void inserirInvestimento(Long id, Long clienteId, String tipo, BigDecimal valor, LocalDate data) {
        em.createNativeQuery("INSERT INTO INVESTIMENTOS (ID, CLIENTE_ID, TIPO, VALOR, DATA) VALUES (?,?,?,?,?)")
                .setParameter(1, id)
                .setParameter(2, clienteId)
                .setParameter(3, tipo)
                .setParameter(4, valor)
                .setParameter(5, data)
                .executeUpdate();
    }

    @Test
    @DisplayName("GET /perfil-risco/{id} sem histórico retorna CONSERVADOR")
    @TestSecurity(user = "admin", roles = {"admin"})
    void perfilSemHistorico() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/perfil-risco/999")
        .then()
            .statusCode(200)
            .body("perfil", equalTo("CONSERVADOR"))
            .body("pontuacao", equalTo(0));
    }

    @Test
    @DisplayName("GET /perfil-risco/{id} historico médio gera MODERADO")
    @TestSecurity(user = "admin", roles = {"admin"})
    void perfilModerado() {
        // Pontuação esperada: volume ~ 6000 -> 20, frequencia 2 -> 20, preferencia alta liquidez (2/2) -> 10 => 50 -> MODERADO
        inserirInvestimento(1L, 100L, "CDB", BigDecimal.valueOf(3000), LocalDate.now().minusDays(5));
        inserirInvestimento(2L, 100L, "LCI", BigDecimal.valueOf(3000), LocalDate.now().minusDays(2));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/perfil-risco/100")
        .then()
            .statusCode(200)
            .body("perfil", equalTo("MODERADO"))
            .body("pontuacao", equalTo(50));
    }

    @Test
    @DisplayName("GET /perfil-risco/{id} historico alto gera AGRESSIVO")
    @TestSecurity(user = "admin", roles = {"admin"})
    void perfilAgressivo() {
        // Pontuação: volume >20000 ->30, frequencia >=5 ->30, preferencia baixa liquidez (apenas 1 de alta liquidez em 6) ->20 => 80 AGRESSIVO
        inserirInvestimento(10L, 200L, "ACOES", BigDecimal.valueOf(10000), LocalDate.now().minusDays(10));
        inserirInvestimento(11L, 200L, "FII", BigDecimal.valueOf(8000), LocalDate.now().minusDays(9));
        inserirInvestimento(12L, 200L, "CRIPTO", BigDecimal.valueOf(5000), LocalDate.now().minusDays(8));
        inserirInvestimento(13L, 200L, "ETFS", BigDecimal.valueOf(4000), LocalDate.now().minusDays(7));
        inserirInvestimento(14L, 200L, "CDB", BigDecimal.valueOf(2000), LocalDate.now().minusDays(6));
        inserirInvestimento(15L, 200L, "ACOES", BigDecimal.valueOf(3000), LocalDate.now().minusDays(5));

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/perfil-risco/200")
        .then()
            .statusCode(200)
            .body("perfil", equalTo("AGRESSIVO"))
            .body("pontuacao", equalTo(80));
    }

    @Test
    @DisplayName("GET /perfil-risco/{id} id inválido retorna 500 (IllegalArgumentException)")
    @TestSecurity(user = "admin", roles = {"admin"})
    void perfilIdInvalido() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/perfil-risco/0")
        .then()
            .statusCode(500); // sem mapper customizado, IllegalArgumentException vira 500
    }
}
