package br.gov.caixa.application.service;

import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilFaixaFrequenciaEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilFaixaVolumeEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilInvestidorFaixaPontuacaoEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilPreferenciaLiquidezEntity;
import br.gov.caixa.domain.enums.PerfilInvestidor;
import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import br.gov.caixa.domain.port.out.ParametrosPerfilRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class PerfilRiscoServiceTest {

    private PerfilRiscoService service;
    private InvestimentoRepository investimentoRepository;
    private ParametrosPerfilRepository parametrosRepository;

    @BeforeEach
    void setUp() {
        service = new PerfilRiscoService();
        investimentoRepository = mock(InvestimentoRepository.class);
        service.investimentoRepository = investimentoRepository; // mesmo package
        parametrosRepository = mock(ParametrosPerfilRepository.class);
        service.parametrosRepository = parametrosRepository;
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
        PerfilFaixaVolumeEntity volumeEntity = mock(PerfilFaixaVolumeEntity.class);
        when(volumeEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorVolume(any(BigDecimal.class))).thenReturn(Optional.of(volumeEntity));

        PerfilFaixaFrequenciaEntity freqEntity = mock(PerfilFaixaFrequenciaEntity.class);
        when(freqEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorFrequencia(anyInt())).thenReturn(Optional.of(freqEntity));

        PerfilPreferenciaLiquidezEntity prefEntity = mock(PerfilPreferenciaLiquidezEntity.class);
        when(prefEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorPreferencia(anyDouble())).thenReturn(Optional.of(prefEntity));

        PerfilInvestidorFaixaPontuacaoEntity pontuacaoEntity = mock(PerfilInvestidorFaixaPontuacaoEntity.class);
        when(pontuacaoEntity.getPerfil()).thenReturn("CONSERVADOR");
        when(parametrosRepository.encontrarFaixaPorPontuacao(30)).thenReturn(Optional.of(pontuacaoEntity));

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
        PerfilFaixaVolumeEntity volumeEntity = mock(PerfilFaixaVolumeEntity.class);
        when(volumeEntity.getPontos()).thenReturn(20);
        when(parametrosRepository.encontrarFaixaPorVolume(any(BigDecimal.class))).thenReturn(Optional.of(volumeEntity));

        PerfilFaixaFrequenciaEntity freqEntity = mock(PerfilFaixaFrequenciaEntity.class);
        when(freqEntity.getPontos()).thenReturn(20);
        when(parametrosRepository.encontrarFaixaPorFrequencia(anyInt())).thenReturn(Optional.of(freqEntity));

        PerfilPreferenciaLiquidezEntity prefEntity = mock(PerfilPreferenciaLiquidezEntity.class);
        when(prefEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorPreferencia(anyDouble())).thenReturn(Optional.of(prefEntity));

        PerfilInvestidorFaixaPontuacaoEntity pontuacaoEntity = mock(PerfilInvestidorFaixaPontuacaoEntity.class);
        when(pontuacaoEntity.getPerfil()).thenReturn("MODERADO");
        when(parametrosRepository.encontrarFaixaPorPontuacao(50)).thenReturn(Optional.of(pontuacaoEntity));

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
        PerfilFaixaVolumeEntity volumeEntity = mock(PerfilFaixaVolumeEntity.class);
        when(volumeEntity.getPontos()).thenReturn(30);
        when(parametrosRepository.encontrarFaixaPorVolume(any(BigDecimal.class))).thenReturn(Optional.of(volumeEntity));

        PerfilFaixaFrequenciaEntity freqEntity = mock(PerfilFaixaFrequenciaEntity.class);
        when(freqEntity.getPontos()).thenReturn(30);
        when(parametrosRepository.encontrarFaixaPorFrequencia(anyInt())).thenReturn(Optional.of(freqEntity));

        PerfilPreferenciaLiquidezEntity prefEntity = mock(PerfilPreferenciaLiquidezEntity.class);
        when(prefEntity.getPontos()).thenReturn(20);
        when(parametrosRepository.encontrarFaixaPorPreferencia(anyDouble())).thenReturn(Optional.of(prefEntity));

        PerfilInvestidorFaixaPontuacaoEntity pontuacaoEntity = mock(PerfilInvestidorFaixaPontuacaoEntity.class);
        when(pontuacaoEntity.getPerfil()).thenReturn("AGRESSIVO");
        when(parametrosRepository.encontrarFaixaPorPontuacao(80)).thenReturn(Optional.of(pontuacaoEntity));

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
        PerfilFaixaVolumeEntity volumeEntity = mock(PerfilFaixaVolumeEntity.class);
        when(volumeEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorVolume(any(BigDecimal.class))).thenReturn(Optional.of(volumeEntity));

        PerfilFaixaFrequenciaEntity freqEntity = mock(PerfilFaixaFrequenciaEntity.class);
        when(freqEntity.getPontos()).thenReturn(20);
        when(parametrosRepository.encontrarFaixaPorFrequencia(anyInt())).thenReturn(Optional.of(freqEntity));

        PerfilPreferenciaLiquidezEntity prefEntity = mock(PerfilPreferenciaLiquidezEntity.class);
        when(prefEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorPreferencia(anyDouble())).thenReturn(Optional.of(prefEntity));

        PerfilInvestidorFaixaPontuacaoEntity pontuacaoEntity = mock(PerfilInvestidorFaixaPontuacaoEntity.class);
        when(pontuacaoEntity.getPerfil()).thenReturn("CONSERVADOR");
        when(parametrosRepository.encontrarFaixaPorPontuacao(40)).thenReturn(Optional.of(pontuacaoEntity));

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
        PerfilFaixaVolumeEntity volumeEntity = mock(PerfilFaixaVolumeEntity.class);
        when(volumeEntity.getPontos()).thenReturn(30);
        when(parametrosRepository.encontrarFaixaPorVolume(any(BigDecimal.class))).thenReturn(Optional.of(volumeEntity));

        PerfilFaixaFrequenciaEntity freqEntity = mock(PerfilFaixaFrequenciaEntity.class);
        when(freqEntity.getPontos()).thenReturn(30);
        when(parametrosRepository.encontrarFaixaPorFrequencia(anyInt())).thenReturn(Optional.of(freqEntity));

        PerfilPreferenciaLiquidezEntity prefEntity = mock(PerfilPreferenciaLiquidezEntity.class);
        when(prefEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorPreferencia(anyDouble())).thenReturn(Optional.of(prefEntity));

        PerfilInvestidorFaixaPontuacaoEntity pontuacaoEntity = mock(PerfilInvestidorFaixaPontuacaoEntity.class);
        when(pontuacaoEntity.getPerfil()).thenReturn("MODERADO");
        when(parametrosRepository.encontrarFaixaPorPontuacao(70)).thenReturn(Optional.of(pontuacaoEntity));

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

    @Test
    @DisplayName("Deve lançar exceção quando não conseguir determinar o perfil")
    void naoConsegueDeterminarPerfil() {
        // Simula pontos calculados, mas sem faixa de pontuação correspondente
        PerfilFaixaVolumeEntity volumeEntity = mock(PerfilFaixaVolumeEntity.class);
        when(volumeEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorVolume(any(BigDecimal.class))).thenReturn(Optional.of(volumeEntity));

        PerfilFaixaFrequenciaEntity freqEntity = mock(PerfilFaixaFrequenciaEntity.class);
        when(freqEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorFrequencia(anyInt())).thenReturn(Optional.of(freqEntity));

        PerfilPreferenciaLiquidezEntity prefEntity = mock(PerfilPreferenciaLiquidezEntity.class);
        when(prefEntity.getPontos()).thenReturn(10);
        when(parametrosRepository.encontrarFaixaPorPreferencia(anyDouble())).thenReturn(Optional.of(prefEntity));

        // Não mocka encontrarFaixaPorPontuacao, ou mocka para retornar empty
        when(parametrosRepository.encontrarFaixaPorPontuacao(30)).thenReturn(Optional.empty());

        when(investimentoRepository.listarPorCliente(6L)).thenReturn(List.of(inv(1,6,"CDB", 3000)));

        NegocioException exception = assertThrows(NegocioException.class, () -> service.consultar(6L));
        assertEquals("Não foi possível determinar o perfil do investidor.", exception.getMessage());
    }
}
