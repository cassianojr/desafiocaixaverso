package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.out.SimulacaoRepository;
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
class SimulacoesResourceIT {

    @Inject
    EntityManager em;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM SimulacaoEntity").executeUpdate();
        em.flush();
    }

    @Test
    @DisplayName("GET /simulacoes vazio retorna lista vazia")
    @TestSecurity(user = "admin", roles = {"admin"})
    void listarSimulacoesVazio() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/simulacoes")
        .then()
            .statusCode(200)
            .body("size()", equalTo(0));
    }

    @Test
    @DisplayName("GET /simulacoes retorna historico de simulacoes persistidas")
    @TestSecurity(user = "admin", roles = {"admin"})
    void listarSimulacoesComDados() {
        LocalDate hoje = LocalDate.now();
        simulacaoRepository.salvarSimulacao(1L, "CDB", BigDecimal.valueOf(1000), BigDecimal.valueOf(1120), 12, hoje);
        simulacaoRepository.salvarSimulacao(2L, "LCI", BigDecimal.valueOf(5000), BigDecimal.valueOf(5600), 24, hoje);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/simulacoes")
        .then()
            .statusCode(200)
            .body("size()", equalTo(2))
            .body("produto", hasItems("CDB", "LCI"))
            .body("find { it.produto == 'CDB' }.valorInvestido", equalTo(1000))
            .body("find { it.produto == 'LCI' }.prazoMeses", equalTo(24));
    }

    @Test
    @DisplayName("GET /simulacoes/por-produto-dia agrupa por produto e dia com media e quantidade")
    @TestSecurity(user = "admin", roles = {"admin"})
    void listarAgrupadoPorProdutoDia() {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        simulacaoRepository.salvarSimulacao(1L, "CDB", BigDecimal.valueOf(1000), BigDecimal.valueOf(1100), 12, hoje);
        simulacaoRepository.salvarSimulacao(2L, "CDB", BigDecimal.valueOf(2000), BigDecimal.valueOf(2300), 24, hoje);
        simulacaoRepository.salvarSimulacao(3L, "LCI", BigDecimal.valueOf(3000), BigDecimal.valueOf(3330), 18, ontem);
        simulacaoRepository.salvarSimulacao(4L, "CDB", BigDecimal.valueOf(1500), BigDecimal.valueOf(1600), 6, ontem);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/simulacoes/por-produto-dia")
        .then()
            .statusCode(200)
            .body("size()", greaterThanOrEqualTo(3))
            .body("produto", hasItems("CDB", "LCI"))
            .body("findAll { it.produto == 'CDB' }.quantidadeSimulacoes.sum()", greaterThanOrEqualTo(3))
            .body("find { it.produto == 'LCI' }.quantidadeSimulacoes", equalTo(1));
    }
}
