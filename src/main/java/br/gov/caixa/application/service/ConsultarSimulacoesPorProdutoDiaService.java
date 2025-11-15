package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesPorProdutoDiaUseCase;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import jakarta.inject.Inject;

import java.util.List;

public class ConsultarSimulacoesPorProdutoDiaService implements ConsultarSimulacoesPorProdutoDiaUseCase {

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Override
    public List<SimulacaoPorProdutoDia> consultar() {
        return simulacaoRepository.agruparPorProdutoEDia();
    }
}
