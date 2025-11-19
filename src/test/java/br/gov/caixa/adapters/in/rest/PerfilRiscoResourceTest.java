package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.in.ConsultarPerfilRiscoUseCase;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class PerfilRiscoResourceTest {

    ConsultarPerfilRiscoUseCase useCase;
    PerfilRiscoResource resource;

    @BeforeEach
    void setup() {
        useCase = mock(ConsultarPerfilRiscoUseCase.class);
        resource = new PerfilRiscoResource();
        resource.consultarPerfilRisco = useCase; // injeção manual
    }

    @Test
    @DisplayName("GET /perfil-risco/{id} retorna perfil e pontuação")
    void consultarPerfilOk() {
        PerfilRiscoResultado resultado = new PerfilRiscoResultado(1L, "MODERADO", 55, "Perfil equilibrado entre segurança e rentabilidade.");
        when(useCase.consultar(1L)).thenReturn(resultado);

        PerfilRiscoResultado r = resource.consultarPerfilRisco(1L);
        assertEquals("MODERADO", r.perfil());
        assertEquals(55, r.pontuacao());
        verify(useCase).consultar(1L);
    }

    @Test
    @DisplayName("GET /perfil-risco/{id} com id inválido propaga exceção")
    void consultarPerfilIdInvalido() {
        when(useCase.consultar(0L)).thenThrow(new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "ID do cliente inválido"));
        NegocioException ex = assertThrows(NegocioException.class, () -> resource.consultarPerfilRisco(0L));
        assertEquals("ID do cliente inválido", ex.getMessage());
        verify(useCase).consultar(0L);
    }
}
