package br.gov.caixa.domain.exception;

import br.gov.caixa.adapters.in.rest.ErrorResponse;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
class NotFoundExceptionMapperTest {

    NotFoundExceptionMapper mapper = new NotFoundExceptionMapper();

    @Test
    @DisplayName("Mapper deve retornar 404 e ErrorResponse com mensagem")
    void mapNotFoundWithMessage() {
        NotFoundException ex = new NotFoundException("Recurso inexistente");
        Response r = mapper.toResponse(ex);

        assertEquals(404, r.getStatus());
        assertEquals("application/json", r.getMediaType().toString());
        assertTrue(r.getEntity() instanceof ErrorResponse);
        ErrorResponse er = (ErrorResponse) r.getEntity();
        assertEquals("Recurso inexistente", er.getMensagem());
        assertNotNull(er.getTimestamp());
    }

    @Test
    @DisplayName("Mapper com mensagem nula mantém entidade com mensagem nula")
    void mapNotFoundNullMessage() {
        NotFoundException ex = new NotFoundException((String) null);
        Response r = mapper.toResponse(ex);
        ErrorResponse er = (ErrorResponse) r.getEntity();
        assertNull(er.getMensagem());
        assertEquals(404, r.getStatus());
    }
}
