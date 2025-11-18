package br.gov.caixa.adapters.in.rest;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.port.in.ConsultarProdutosRecomendadosUseCase;
import io.quarkus.test.junit.QuarkusTest;
import org.jboss.resteasy.reactive.RestResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ProdutosRecomendadosResourceTest {

    ConsultarProdutosRecomendadosUseCase useCase;
    ProdutosRecomendadosResource resource;

    @BeforeEach
    void setup() {
        useCase = mock(ConsultarProdutosRecomendadosUseCase.class);
        resource = new ProdutosRecomendadosResource();
        resource.consultar = useCase; // injeção manual do mock
    }

    @Test
    @DisplayName("GET /produtos-recomendados/{perfil} retorna lista de produtos para perfil válido")
    void consultarPerfilValido() {
        Produto p1 = new Produto(1L, "CDB Banco", "CDB", BigDecimal.valueOf(0.12), "BAIXO");
        when(useCase.consultar("CONSERVADOR")).thenReturn(List.of(p1));

        var response = resource.consultar("CONSERVADOR");

        assertEquals(200, response.getStatus());
        assertTrue(response.getEntity() instanceof List<?>);
        @SuppressWarnings("unchecked")
        List<Produto> lista = (List<Produto>) response.getEntity();
        assertEquals(1, lista.size());
        assertEquals("CDB", lista.getFirst().tipo());
        verify(useCase).consultar("CONSERVADOR");
    }

    @Test
    @DisplayName("GET /produtos-recomendados/{perfil} lança erro para perfil inválido")
    void consultarPerfilInvalido() {
        NegocioException ex = new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "Perfil do investidor inválido");
        when(useCase.consultar("XYZ")).thenThrow(ex);

        NegocioException thrown = assertThrows(NegocioException.class, () -> resource.consultar("XYZ"));
        assertEquals("Perfil do investidor inválido", thrown.getMessage());
        verify(useCase).consultar("XYZ");
    }
}
