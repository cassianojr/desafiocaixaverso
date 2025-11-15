package br.gov.caixa.domain.port.in;

import br.gov.caixa.domain.model.Investimento;

import java.util.List;

public interface ConsultarInvestimentosUseCase {
    List<Investimento> consultar(Long clienteId);
}
