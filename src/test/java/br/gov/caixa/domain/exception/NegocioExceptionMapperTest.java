package br.gov.caixa.domain.exception;

import br.gov.caixa.adapters.in.rest.ErrorResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class NegocioExceptionMapperTest {

    NegocioExceptionMapper mapper = new NegocioExceptionMapper();

    @Test
    @DisplayName("Mapper retorna status BAD_REQUEST e ErrorResponse com mensagem")
    void mapBadRequest() {
        NegocioException ex = new NegocioException(400, "Dados inválidos");
        Response r = mapper.toResponse(ex);
        assertEquals(400, r.getStatus());
        assertTrue(r.getEntity() instanceof ErrorResponse);
        ErrorResponse er = (ErrorResponse) r.getEntity();
        assertEquals("Dados inválidos", er.getMensagem());
        assertNotNull(er.getTimestamp());
    }

    @Test
    @DisplayName("Mapper mantém código NOT_FOUND quando informado")
    void mapNotFound() {
        NegocioException ex = new NegocioException(404, "Produto não encontrado");
        Response r = mapper.toResponse(ex);
        assertEquals(404, r.getStatus());
        ErrorResponse er = (ErrorResponse) r.getEntity();
        assertEquals("Produto não encontrado", er.getMensagem());
    }

    @Test
    @DisplayName("Mapper aceita mensagem nula sem falhar")
    void mapNullMessage() {
        NegocioException ex = new NegocioException(422, null);
        Response r = mapper.toResponse(ex);
        assertEquals(422, r.getStatus());
        ErrorResponse er = (ErrorResponse) r.getEntity();
        assertNull(er.getMensagem());
        assertNotNull(er.getTimestamp());
    }
}
