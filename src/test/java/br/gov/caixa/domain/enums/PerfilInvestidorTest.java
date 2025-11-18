package br.gov.caixa.domain.enums;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class PerfilInvestidorTest {

    @Test
    @DisplayName("riscoPadrao devolve riscos corretos para cada perfil")
    void riscoPadraoCorreto() {
        assertEquals(Risco.BAIXO, PerfilInvestidor.CONSERVADOR.riscoPadrao());
        assertEquals(Risco.MEDIO, PerfilInvestidor.MODERADO.riscoPadrao());
        assertEquals(Risco.ALTO, PerfilInvestidor.AGRESSIVO.riscoPadrao());
    }

    @Test
    @DisplayName("fromString aceita valores case-insensitive e retorna enum correto")
    void fromStringCaseInsensitive() {
        assertEquals(PerfilInvestidor.CONSERVADOR, PerfilInvestidor.fromString("conservador"));
        assertEquals(PerfilInvestidor.MODERADO, PerfilInvestidor.fromString("MoDeRaDo"));
        assertEquals(PerfilInvestidor.AGRESSIVO, PerfilInvestidor.fromString("AGRESSIVO"));
    }

    @Test
    @DisplayName("fromString inválido retorna null")
    void fromStringInvalido() {
        assertNull(PerfilInvestidor.fromString(""));
        assertNull(PerfilInvestidor.fromString("XPTO"));
        assertNull(PerfilInvestidor.fromString("moderados"));
    }

    @Test
    @DisplayName("isValid reconhece somente três perfis e é case-insensitive")
    void isValidTest() {
        assertTrue(PerfilInvestidor.isValid("Conservador"));
        assertTrue(PerfilInvestidor.isValid("MODERADO"));
        assertTrue(PerfilInvestidor.isValid("agressivo"));
        assertFalse(PerfilInvestidor.isValid(""));
        assertFalse(PerfilInvestidor.isValid("agressiv"));
        assertFalse(PerfilInvestidor.isValid("investidor"));
    }
}
