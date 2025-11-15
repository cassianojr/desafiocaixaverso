package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.port.in.ConsultarSimulacoesUseCase;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ConsultarSimulacoesService implements ConsultarSimulacoesUseCase{

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Override
    public List<SimulacaoHistorico> listarTodas() {
        return simulacaoRepository.listarTodas();
    }
}
