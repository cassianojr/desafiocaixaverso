package br.gov.caixa.application.service;

import br.gov.caixa.domain.enums.Risco;
import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.port.out.ProdutoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ConsultarProdutosRecomendadosServiceTest {

    private ConsultarProdutosRecomendadosService service;
    private ProdutoRepository produtoRepository;

    @BeforeEach
    void setUp() {
        service = new ConsultarProdutosRecomendadosService();
        produtoRepository = mock(ProdutoRepository.class);
        service.produtoRepository = produtoRepository; // mesmo package para injeção
    }

    private Produto prod(long id, String nome, String tipo, double rent, String risco) {
        return new Produto(id, nome, tipo, BigDecimal.valueOf(rent), risco);
    }

    @Test
    @DisplayName("Deve lançar NegocioException para perfil inválido")
    void perfilInvalido() {
        NegocioException ex1 = assertThrows(NegocioException.class, () -> service.consultar(null));
        NegocioException ex2 = assertThrows(NegocioException.class, () -> service.consultar(""));
        NegocioException ex3 = assertThrows(NegocioException.class, () -> service.consultar("desconhecido"));
        assertEquals(400, ex1.getCodigo());
        assertEquals(400, ex2.getCodigo());
        assertEquals(400, ex3.getCodigo());
        verifyNoInteractions(produtoRepository);
    }

    @Test
    @DisplayName("Perfil conservador deve buscar risco BAIXO")
    void perfilConservador() {
        List<Produto> lista = List.of(
                prod(1,"CDB Premium","CDB",0.12,"BAIXO"),
                prod(2,"LCI Garantida","LCI",0.10,"BAIXO")
        );
        when(produtoRepository.findByRisco(Risco.BAIXO.name())).thenReturn(lista);

        List<Produto> resultado = service.consultar("conservador");

        assertEquals(lista, resultado);
        verify(produtoRepository).findByRisco(Risco.BAIXO.name());
    }

    @Test
    @DisplayName("Perfil moderado deve buscar risco MEDIO")
    void perfilModerado() {
        List<Produto> lista = List.of(
                prod(3,"FII Log","FII",0.15,"MEDIO"),
                prod(4,"ETF IBOV","ETF",0.18,"MEDIO")
        );
        when(produtoRepository.findByRisco(Risco.MEDIO.name())).thenReturn(lista);

        List<Produto> resultado = service.consultar("moderado");

        assertEquals(2, resultado.size());
        assertEquals("FII Log", resultado.get(0).nome());
        verify(produtoRepository).findByRisco(Risco.MEDIO.name());
    }

    @Test
    @DisplayName("Perfil agressivo deve buscar risco ALTO")
    void perfilAgressivo() {
        List<Produto> lista = List.of(
                prod(5,"Acoes Tech","ACOES",0.25,"ALTO"),
                prod(6,"Crypto Index","CRYPTO",0.40,"ALTO")
        );
        when(produtoRepository.findByRisco(Risco.ALTO.name())).thenReturn(lista);

        List<Produto> resultado = service.consultar("agressivo");

        assertEquals(2, resultado.size());
        assertTrue(resultado.stream().allMatch(p -> p.risco().equals("ALTO")));
        verify(produtoRepository).findByRisco(Risco.ALTO.name());
    }

    @Test
    @DisplayName("Case-insensitive do perfil")
    void perfilCaseInsensitive() {
        when(produtoRepository.findByRisco(Risco.MEDIO.name())).thenReturn(List.of());
        List<Produto> resultado = service.consultar("MoDeRaDo");
        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(produtoRepository).findByRisco(Risco.MEDIO.name());
    }
}
