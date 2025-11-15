package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.port.in.ConsultarInvestimentosUseCase;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ConsultarInvestimentosService implements ConsultarInvestimentosUseCase {

    @Inject
    InvestimentoRepository investimentoRepository;

    @Override
    public List<Investimento> consultar(Long clienteId) {
        return investimentoRepository.listarPorCliente(clienteId);
    }
}
