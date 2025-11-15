package br.gov.caixa.domain.port.in;

import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoResultado;

public interface SimularInvestimentoUseCase {
    SimulacaoResultado executar(Simulacao simulacao);
}
