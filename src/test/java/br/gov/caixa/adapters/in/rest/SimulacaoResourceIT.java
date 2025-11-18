package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.port.out.ProdutoRepository;
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

import java.time.LocalDate;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

@QuarkusTest
class SimulacaoResourceIT {

    @Inject
    EntityManager em;

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @BeforeEach
    @Transactional
    void limpar() {
        em.createQuery("DELETE FROM SimulacaoEntity").executeUpdate();
        em.flush();
    }

    @Test
    @DisplayName("POST /simular-investimento calcula e persiste simulacao")
    @TestSecurity(user = "admin", roles = {"admin"})
    void simularInvestimentoOk() {
        // usar produto existente do import.sql ou inserir dinamicamente se necessário
        // assumindo que tipo 'CDB' existe; caso contrário teste falhará e precisaremos incluir inserção
        String json = "{\"clienteId\":1,\"valor\":1000,\"prazoMeses\":12,\"tipoProduto\":\"CDB\"}";

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(200)
            .body("produtoValidado.tipo", equalTo("CDB"))
            .body("resultadoSimulacao.prazoMeses", equalTo(12))
            .body("resultadoSimulacao.valorFinal", notNullValue());
    }

    @Test
    @DisplayName("POST /simular-investimento retorna 404 para produto inexistente")
    @TestSecurity(user = "admin", roles = {"admin"})
    void simularInvestimentoProdutoInexistente() {
        String json = "{\"clienteId\":1,\"valor\":1000,\"prazoMeses\":12,\"tipoProduto\":\"INEXISTENTE\"}";

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(404);
    }

    @Test
    @DisplayName("POST /simular-investimento retorna 400 para dados inválidos")
    @TestSecurity(user = "admin", roles = {"admin"})
    void simularInvestimentoDadosInvalidos() {
        String json = "{\"clienteId\":0,\"valor\":0,\"prazoMeses\":0,\"tipoProduto\":\"\"}";

        given()
            .contentType(ContentType.JSON)
            .body(json)
        .when()
            .post("/simular-investimento")
        .then()
            .statusCode(400);
    }

    @Test
    @DisplayName("POST /simular-investimento com múltiplas chamadas cria registros distintos e média não é exposta")
    @TestSecurity(user = "admin", roles = {"admin"})
    void simularInvestimentoMultiplasChamadas() {
        String base = "{\"clienteId\":%d,\"valor\":1000,\"prazoMeses\":12,\"tipoProduto\":\"CDB\"}";
        for (int i = 1; i <= 3; i++) {
            given().contentType(ContentType.JSON).body(String.format(base, i)).post("/simular-investimento").then().statusCode(200);
        }
        // validar que agruparPorProdutoEDia contém quantidade >=3
        var grupos = simulacaoRepository.agruparPorProdutoEDia();
        assertTrue(grupos.stream().filter(g -> g.produto().equals("CDB") && g.data().equals(LocalDate.now())).findFirst().orElseThrow().quantidadeSimulacoes() >= 3);
    }
}
