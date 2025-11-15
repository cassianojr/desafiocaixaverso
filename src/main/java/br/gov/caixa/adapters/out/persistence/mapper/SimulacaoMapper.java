package br.gov.caixa.adapters.out.persistence.mapper;

import br.gov.caixa.adapters.out.persistence.entity.SimulacaoEntity;
import br.gov.caixa.domain.model.SimulacaoHistorico;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SimulacaoMapper {
    public SimulacaoHistorico toDomain(SimulacaoEntity e) {
        return new SimulacaoHistorico(
                e.getId(),
                e.getClienteId(),
                e.getProduto(),
                e.getValorInvestido(),
                e.getValorFinal(),
                e.getPrazoMeses(),
                e.getDataSimulacao()
        );
    }
}
