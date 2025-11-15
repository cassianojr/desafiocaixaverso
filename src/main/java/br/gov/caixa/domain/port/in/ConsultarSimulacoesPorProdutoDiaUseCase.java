package br.gov.caixa.domain.port.in;

import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;

import java.util.List;

public interface ConsultarSimulacoesPorProdutoDiaUseCase {
    List<SimulacaoPorProdutoDia> consultar();
}
