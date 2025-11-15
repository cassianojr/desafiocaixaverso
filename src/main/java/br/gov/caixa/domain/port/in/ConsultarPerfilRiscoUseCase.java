package br.gov.caixa.domain.port.in;

import br.gov.caixa.domain.model.PerfilRiscoResultado;

public interface ConsultarPerfilRiscoUseCase {
    PerfilRiscoResultado consultar(Long clienteId);
}
