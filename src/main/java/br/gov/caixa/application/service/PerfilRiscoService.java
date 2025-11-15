package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.model.PerfilRisco;
import br.gov.caixa.domain.model.PerfilRiscoResultado;
import br.gov.caixa.domain.port.in.ConsultarPerfilRiscoUseCase;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class PerfilRiscoService implements ConsultarPerfilRiscoUseCase {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Override
    public PerfilRiscoResultado consultar(Long clienteId) {
        List<Investimento> historico = investimentoRepository.listarPorCliente(clienteId);

        if(historico.isEmpty()){
            return new PerfilRiscoResultado(
                    clienteId,
                    "Conservador",
                    0,
                    "Sem histórico de investimentos. Classificado como Conservador."
            );
        }

        // Calcular pontuação baseada no volume total investido
        double volume = historico.stream().mapToDouble(Investimento::valor).sum();
        int pontosVolume = (volume < 5000) ? 10 : (volume < 20000) ? 20 : 30;

        // Calcular pontuação baseada na diversificação dos investimentos
        int quantidade = historico.size();
        int pontosFrequencia = (quantidade < 2)? 10 : (quantidade < 5) ? 20 : 30;

        // Calcular pontuação baseada na liquidez dos investimentos
        long pontosDeAltaLiquidez = historico.stream()
                .filter(investimento -> investimento.tipo().toUpperCase().contains("CDB") ||
                        investimento.tipo().toUpperCase().contains("LCI") ||
                        investimento.tipo().toUpperCase().contains("LCA"))
                .count();

        boolean preferenciaAltaLiquidez = pontosDeAltaLiquidez >= (historico.size() / 2.0);

        int pontosPreferencia = preferenciaAltaLiquidez ? 10 : 20;

        //pontuacao total
        int pontuacaoFinal = pontosVolume + pontosFrequencia + pontosPreferencia;

        String perfil = (pontuacaoFinal <= 40) ? "Conservador" :
                (pontuacaoFinal <= 70) ? "Moderado" : "Agressivo";
        String descricao = switch (perfil) {
            case "Conservador" -> "Busca segurança e baixa variação, priorizando liquidez.";
            case "Moderado" -> "Perfil equilibrado entre segurança e rentabilidade.";
            default -> "Busca maior rentabilidade aceitando riscos mais elevados.";
        };

        return new PerfilRiscoResultado(
                clienteId,
                perfil,
                pontuacaoFinal,
                descricao
        );
    }
}
