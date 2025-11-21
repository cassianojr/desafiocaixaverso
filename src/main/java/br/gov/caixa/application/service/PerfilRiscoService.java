package br.gov.caixa.application.service;

import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilFaixaFrequenciaEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilFaixaVolumeEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilPreferenciaLiquidezEntity;
import br.gov.caixa.domain.enums.PerfilInvestidor;
import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.in.ConsultarPerfilRiscoUseCase;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import br.gov.caixa.domain.port.out.ParametrosPerfilRepository;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class PerfilRiscoService implements ConsultarPerfilRiscoUseCase {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Inject
    ParametrosPerfilRepository parametrosRepository;

    @Override
    public PerfilRiscoResultado consultar(Long clienteId) {

        if (clienteId == null || clienteId <= 0) {
            throw new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "ID do cliente inválido.");
        }

        List<Investimento> historico = investimentoRepository.listarPorCliente(clienteId);

        if (historico.isEmpty()) {
            return new PerfilRiscoResultado(
                    clienteId,
                    PerfilInvestidor.CONSERVADOR.name(),
                    0,
                    "Sem histórico de investimentos. Classificado como Conservador."
            );
        }

        int pontosVolume = calculaPontosVolume(historico);
        int pontosFrequencia = calculaPontosFrequencia(historico);
        int pontosPreferencia = calculaPontosPreferencia(historico);

        int pontuacaoFinal = pontosVolume + pontosFrequencia + pontosPreferencia;

        PerfilInvestidor perfil = calcularPerfilInvestidor(pontuacaoFinal);
        if(perfil == null) {
            throw new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "Não foi possível determinar o perfil do investidor.");
        }

        String descricao = buscarDescricaoPerfil(perfil);

        return new PerfilRiscoResultado(
                clienteId,
                perfil.name(),
                pontuacaoFinal,
                descricao
        );
    }

    private int calculaPontosVolume(List<Investimento> historico) {
        BigDecimal volumeTotal = historico.stream()
                .map(Investimento::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return parametrosRepository.encontrarFaixaPorVolume(volumeTotal)
                .map(PerfilFaixaVolumeEntity::getPontos)
                .orElse(0);
    }

    private int calculaPontosFrequencia(List<Investimento> historico) {
        int quantidade = historico.size();

        return parametrosRepository.encontrarFaixaPorFrequencia(quantidade)
                .map(PerfilFaixaFrequenciaEntity::getPontos)
                .orElse(0);
    }

    private int calculaPontosPreferencia(List<Investimento> historico) {

        long altaLiquidez = historico.stream()
                .filter(i -> {
                    String tipo = i.tipo().toUpperCase();
                    return tipo.contains("CDB") || tipo.contains("LCI") || tipo.contains("LCA");
                })
                .count();

        double percentual = (altaLiquidez * 100.0) / historico.size();

        return parametrosRepository.encontrarFaixaPorPreferencia(percentual)
                .map(PerfilPreferenciaLiquidezEntity::getPontos)
                .orElse(0);
    }

    private PerfilInvestidor calcularPerfilInvestidor(int pontuacaoFinal) {
        return parametrosRepository.encontrarFaixaPorPontuacao(pontuacaoFinal)
                .map(faixa -> PerfilInvestidor.valueOf(faixa.getPerfil()))
                .orElse(null);
    }

    private String buscarDescricaoPerfil(PerfilInvestidor perfil) {
        return perfil == PerfilInvestidor.CONSERVADOR
                ? "Busca segurança e baixa variação, priorizando liquidez."
                : perfil == PerfilInvestidor.MODERADO
                ? "Perfil equilibrado entre segurança e rentabilidade."
                : "Busca maior rentabilidade aceitando riscos mais elevados.";
    }
}
