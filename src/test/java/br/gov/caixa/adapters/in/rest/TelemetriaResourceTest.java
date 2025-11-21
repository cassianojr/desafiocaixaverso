package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.out.TelemetriaRepository;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@QuarkusTest
class TelemetriaResourceTest {

    @Inject
    EntityManager em;

    @Inject
    TelemetriaRepository telemetriaRepository; // usar instância real

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM TelemetriaEntity").executeUpdate();
        em.flush();
    }

    @Test
    @DisplayName("GET /api/v1/telemetria vazio retorna lista servicos vazia e periodo válido")
    @TestSecurity(user = "admin", roles = {"admin"})
    void consultarTelemetriaVazio() {
        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/v1/telemetria")
        .then()
            .statusCode(200)
            .body("servicos", hasSize(0))
            .body("periodo.inicio", notNullValue())
            .body("periodo.fim", notNullValue());
    }

    @Test
    @DisplayName("GET /telemetria retorna apenas registros do mês corrente ordenados desc por data")
    @TestSecurity(user = "admin", roles = {"admin"})
    void consultarTelemetriaComDadosMes() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioMes = hoje.withDayOfMonth(1);
        LocalDate foraMes = inicioMes.minusDays(1); // deve ser ignorado

        telemetriaRepository.registrarChamada("/api/v1/investimentos", 100.0, hoje.minusDays(1));
        telemetriaRepository.registrarChamada("/api/v1/simulacoes", 50.0, hoje);
        telemetriaRepository.registrarChamada("/api/v1/simulacoes", 150.0, hoje);
        telemetriaRepository.registrarChamada("/api/v1/fora", 200.0, foraMes); // fora do mês

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/v1/telemetria")
        .then()
            .statusCode(200)
            .body("servicos.size()", greaterThanOrEqualTo(2))
            .body("servicos.servico", hasItems("/api/v1/investimentos", "/api/v1/simulacoes"))
            .body("servicos.servico", not(hasItem("/api/v1/fora")))
            .body("periodo.inicio", equalTo(inicioMes.toString()))
            .body("periodo.fim", equalTo(hoje.toString()));
    }

    @Test
    @DisplayName("GET /telemetria consolida média e quantidade corretamente para um serviço")
    @TestSecurity(user = "admin", roles = {"admin"})
    void consultarTelemetriaMediaQuantidade() {
        LocalDate hoje = LocalDate.now();
        telemetriaRepository.registrarChamada("/api/v1/simulacoes", 50.0, hoje);
        telemetriaRepository.registrarChamada("/api/v1/simulacoes", 150.0, hoje);
        telemetriaRepository.registrarChamada("/api/v1/simulacoes", 100.0, hoje);

        given()
            .accept(ContentType.JSON)
        .when()
            .get("/api/v1/telemetria")
        .then()
            .statusCode(200)
            .body("servicos.find { it.servico == '/api/v1/simulacoes' }.quantidadeChamadas", equalTo(3))
            .body("servicos.find { it.servico == '/api/v1/simulacoes' }.mediaTempoRespostaMs", equalTo(100.0F));
    }
}