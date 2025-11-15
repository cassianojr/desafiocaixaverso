package br.gov.caixa.domain.port.in;

import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoHistorico;

import java.util.List;

public interface ConsultarSimulacoesUseCase {
    List<SimulacaoHistorico> listarTodas();
}
