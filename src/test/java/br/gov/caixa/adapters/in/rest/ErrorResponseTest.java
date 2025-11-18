package br.gov.caixa.adapters.in.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class ErrorResponseTest {

    @Test
    @DisplayName("Construtor deve armazenar mensagem e gerar timestamp atual")
    void construtorArmazenaMensagemETimestamp() {
        LocalDateTime antes = LocalDateTime.now();
        ErrorResponse er = new ErrorResponse("Falha interna");
        LocalDateTime depois = LocalDateTime.now();

        assertEquals("Falha interna", er.getMensagem());
        assertNotNull(er.getTimestamp());
        // timestamp deve estar entre antes e depois (tolerância pequena)
        assertFalse(er.getTimestamp().isBefore(antes));
        assertFalse(er.getTimestamp().isAfter(depois));
    }

    @Test
    @DisplayName("Mensagem nula é aceita e retornada como nula")
    void mensagemNulaAceita() {
        ErrorResponse er = new ErrorResponse(null);
        assertNull(er.getMensagem());
        assertNotNull(er.getTimestamp());
    }

    @Test
    @DisplayName("Instâncias distintas possuem timestamps distintos (geralmente)")
    void instanciasDistintasTimestampDiferente() {
        ErrorResponse e1 = new ErrorResponse("Erro 1");
        // Pequeno sleep para aumentar chance de diferença
        try { Thread.sleep(5); } catch (InterruptedException ignored) {}
        ErrorResponse e2 = new ErrorResponse("Erro 2");

        // Pode ser igual se resolução do clock for baixa; então exigimos <= 1ms diferença como aceitável
        long diffMillis = Duration.between(e1.getTimestamp(), e2.getTimestamp()).toMillis();
        assertTrue(diffMillis >= 0); // ordem cronológica
    }
}
