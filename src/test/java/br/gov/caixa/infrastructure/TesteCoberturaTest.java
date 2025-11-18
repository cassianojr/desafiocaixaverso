package br.gov.caixa.infrastructure;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import jakarta.inject.Inject;

@QuarkusTest
class TesteCoberturaTest {

    @Inject
    TesteCobertura testeCobertura;

    @Test
    void test() {
        assertEquals(5, testeCobertura.soma(2, 3));
    }

}
