package br.gov.caixa.application.service;

import br.gov.caixa.domain.enums.PerfilInvestidor;
import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.in.ConsultarPerfilRiscoUseCase;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

import java.math.BigDecimal;
import java.util.List;

@ApplicationScoped
public class PerfilRiscoService implements ConsultarPerfilRiscoUseCase {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Override
    public PerfilRiscoResultado consultar(Long clienteId) {

        if(clienteId == null || clienteId <= 0){
            throw new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "ID do cliente inválido.");
        }

        List<Investimento> historico = investimentoRepository.listarPorCliente(clienteId);

        if(historico.isEmpty()){
            return new PerfilRiscoResultado(
                    clienteId,
                    PerfilInvestidor.CONSERVADOR.name(),
                    0,
                    "Sem histórico de investimentos. Classificado como Conservador."
            );
        }

        int pontosVolume = calulaPontosPorVoumeTotal(historico);

        int pontosFrequencia = calculaPontosPorFrequencia(historico);

        int pontosPreferencia = calculaPontosPorPreferencia(historico);

        int pontuacaoFinal = calculaPontuacaoFinal(pontosVolume, pontosFrequencia, pontosPreferencia);

        PerfilInvestidor perfil = (pontuacaoFinal <= 40) ? PerfilInvestidor.CONSERVADOR :
                (pontuacaoFinal <= 70) ? PerfilInvestidor.MODERADO : PerfilInvestidor.AGRESSIVO;

        String descricao = switch (perfil) {
            case PerfilInvestidor.CONSERVADOR -> "Busca segurança e baixa variação, priorizando liquidez.";
            case PerfilInvestidor.MODERADO -> "Perfil equilibrado entre segurança e rentabilidade.";
            default -> "Busca maior rentabilidade aceitando riscos mais elevados.";
        };

        return new PerfilRiscoResultado(
                clienteId,
                perfil.name(),
                pontuacaoFinal,
                descricao
        );
    }

    /**
     * Calcula a pontuação final somando os pontos de volume, frequência e preferência.
     * @param pontosVolume pontos por volume total investido
     * @param pontosFrequencia pontos por frequência de investimentos
     * @param pontosPreferencia pontos por preferência de investimentos
     * @return pontuação final
     */
    private static int calculaPontuacaoFinal(int pontosVolume, int pontosFrequencia, int pontosPreferencia) {
        return pontosVolume + pontosFrequencia + pontosPreferencia;
    }

    /**
     * Calcula os pontos com base na preferência por investimentos de alta liquidez.
     *  Se mais da metade dos investimentos forem de alta liquidez (CDB, LCI, LCA), retorna 10 pontos; caso contrário, retorna 20 pontos.
     * @param historico o histórico de investimentos do cliente
     * @return pontuação baseada na preferência por alta liquidez
     */
    private static int calculaPontosPorPreferencia(List<Investimento> historico) {
        long altaLiquidez = historico.stream()
                .filter(i ->
                        i.tipo().toUpperCase().contains("CDB")
                                || i.tipo().toUpperCase().contains("LCI")
                                || i.tipo().toUpperCase().contains("LCA")
                )
                .count();

        boolean preferenciaAltaLiquidez =
                altaLiquidez >= (historico.size() / 2.0);

        return preferenciaAltaLiquidez ? 10 : 20;
    }

    /**
     * Calcula os pontos com base na frequência de investimentos do cliente.
     * Se o cliente tiver menos de 2 investimentos, retorna 10 pontos;
     * se tiver entre 2 e 4 investimentos, retorna 20 pontos;
     * caso contrário, retorna 30 pontos.
     * @param historico o histórico de investimentos do cliente
     * @return pontuação baseada na frequência de investimentos
     */
    private static int calculaPontosPorFrequencia(List<Investimento> historico) {
        int quantidade = historico.size();
        return (quantidade < 2) ? 10 :
                (quantidade < 5) ? 20 : 30;
    }

    /**
     * Calcula os pontos com base no volume total investido pelo cliente.
     * Se o volume total for menor que R$ 5.000, retorna 10 pontos;
     * se estiver entre R$ 5.000 e R$ 20.000, retorna 20 pontos;
     * caso contrário, retorna 30 pontos.
     * @param historico o histórico de investimentos do cliente
     * @return pontuação baseada no volume total investido
     */
    private static int calulaPontosPorVoumeTotal(List<Investimento> historico) {
        BigDecimal volumeTotal = historico.stream()
                .map(Investimento::valor)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Regra de pontuação usando compareTo
        return (volumeTotal.compareTo(BigDecimal.valueOf(5000)) < 0) ? 10 :
                (volumeTotal.compareTo(BigDecimal.valueOf(20000)) < 0) ? 20 : 30;
    }
}
