package br.gov.caixa.application.service;

import br.gov.caixa.domain.enums.PerfilInvestidor;
import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class PerfilRiscoServiceTest {

    private PerfilRiscoService service;
    private InvestimentoRepository investimentoRepository;

    @BeforeEach
    void setUp() {
        service = new PerfilRiscoService();
        investimentoRepository = mock(InvestimentoRepository.class);
        service.investimentoRepository = investimentoRepository; // mesmo package
    }

    private Investimento inv(long id, long cliente, String tipo, double valor) {
        return new Investimento(id, cliente, tipo, BigDecimal.valueOf(valor), BigDecimal.valueOf(0.10), LocalDate.now());
    }

    @Test
    @DisplayName("Deve lançar exceção para clienteId inválido (null ou <=0)")
    void clienteIdInvalido() {
        assertThrows(NegocioException.class, () -> service.consultar(null));
        assertThrows(NegocioException.class, () -> service.consultar(0L));
        verifyNoInteractions(investimentoRepository);
    }

    @Test
    @DisplayName("Sem histórico: perfil Conservador com pontuação 0 e mensagem adequada")
    void semHistorico() {
        when(investimentoRepository.listarPorCliente(10L)).thenReturn(List.of());
        PerfilRiscoResultado r = service.consultar(10L);
        assertEquals(10L, r.clienteId());
        assertEquals(PerfilInvestidor.CONSERVADOR.name(), r.perfil());
        assertEquals(0, r.pontuacao());
        assertEquals("Sem histórico de investimentos. Classificado como Conservador.", r.descricao());
    }

    @Test
    @DisplayName("Perfil Conservador típico (pontos <= 40)")
    void perfilConservador() {
        // volume <5000 =>10, freq size=1 =>10, preferencia alta liquidez (1/1) =>10 => total 30
        when(investimentoRepository.listarPorCliente(1L)).thenReturn(List.of(inv(1,1,"CDB", 3000)));
        PerfilRiscoResultado r = service.consultar(1L);
        assertEquals(PerfilInvestidor.CONSERVADOR.name(), r.perfil());
        assertEquals(30, r.pontuacao());
        assertEquals("Busca segurança e baixa variação, priorizando liquidez.", r.descricao());
    }

    @Test
    @DisplayName("Perfil Moderado típico (40 < pontos <= 70)")
    void perfilModerado() {
        // volume entre 5000 e 20000 =>20, freq size=3 =>20, preferencia alta liquidez maioria =>10 -> total 50
        when(investimentoRepository.listarPorCliente(2L)).thenReturn(List.of(
                inv(1,2,"CDB", 6000),
                inv(2,2,"LCI", 4000),
                inv(3,2,"FII", 2000)
        ));
        PerfilRiscoResultado r = service.consultar(2L);
        assertEquals(PerfilInvestidor.MODERADO.name(), r.perfil());
        assertEquals(50, r.pontuacao());
        assertEquals("Perfil equilibrado entre segurança e rentabilidade.", r.descricao());
    }

    @Test
    @DisplayName("Perfil Agressivo típico (pontos > 70)")
    void perfilAgressivo() {
        // volume >=20000 =>30, freq size=6 =>30, preferencia baixa liquidez maioria =>20 -> total 80
        when(investimentoRepository.listarPorCliente(3L)).thenReturn(List.of(
                inv(1,3,"ACOES", 5000),
                inv(2,3,"FII", 4000),
                inv(3,3,"DEBENTURES", 3000),
                inv(4,3,"ACOES", 6000),
                inv(5,3,"CRYPTO", 2000),
                inv(6,3,"ETF", 3000)
        ));
        PerfilRiscoResultado r = service.consultar(3L);
        assertEquals(PerfilInvestidor.AGRESSIVO.name(), r.perfil());
        assertEquals(80, r.pontuacao());
        assertEquals("Busca maior rentabilidade aceitando riscos mais elevados.", r.descricao());
    }

    @Test
    @DisplayName("Limite 40 deve ser Conservador")
    void limiteQuarentaConservador() {
        // volume <5000 =>10, freq size=3 =>20, preferencia alta liquidez =>10 -> total 40
        when(investimentoRepository.listarPorCliente(4L)).thenReturn(List.of(
                inv(1,4,"CDB", 1000),
                inv(2,4,"LCI", 1500),
                inv(3,4,"LCA", 1200)
        ));
        PerfilRiscoResultado r = service.consultar(4L);
        assertEquals(PerfilInvestidor.CONSERVADOR.name(), r.perfil());
        assertEquals(40, r.pontuacao());
    }

    @Test
    @DisplayName("Limite 70 deve ser Moderado")
    void limiteSetentaModerado() {
        // volume >=20000 =>30, freq size=5 =>30, preferencia alta liquidez maioria =>10 -> total 70
        when(investimentoRepository.listarPorCliente(5L)).thenReturn(List.of(
                inv(1,5,"CDB", 8000),
                inv(2,5,"LCI", 6000),
                inv(3,5,"LCA", 5000),
                inv(4,5,"FII", 2000),
                inv(5,5,"CDB", 1000)
        ));
        PerfilRiscoResultado r = service.consultar(5L);
        assertEquals(PerfilInvestidor.MODERADO.name(), r.perfil());
        assertEquals(70, r.pontuacao());
    }
}
