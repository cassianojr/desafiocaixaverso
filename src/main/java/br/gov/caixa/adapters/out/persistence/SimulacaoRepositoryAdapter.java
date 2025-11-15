package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.SimulacaoEntity;
import br.gov.caixa.adapters.out.persistence.mapper.SimulacaoMapper;
import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SimulacaoRepositoryAdapter implements SimulacaoRepository {

    @Inject
    EntityManager entityManager;

    @Inject
    SimulacaoMapper simulacaoMapper;

    @Override
    @Transactional
    public void salvarSimulacao(Long aLong, String nome, Double valor, double valorFinal, Integer integer, LocalDateTime localDateTime) {
        SimulacaoEntity simulacao = new SimulacaoEntity();
        simulacao.setClienteId(aLong);
        simulacao.setProduto(nome);
        simulacao.setValorInvestido(valor);
        simulacao.setValorFinal(valorFinal);
        simulacao.setPrazoMeses(integer);
        simulacao.setDataSimulacao(localDateTime);

        entityManager.persist(simulacao);
    }

    @Override
    public List<SimulacaoHistorico> listarTodas() {
        return entityManager.createQuery("from SimulacaoEntity", SimulacaoEntity.class)
                .getResultList()
                .stream()
                .map(simulacaoMapper::toDomain)
                .toList();
    }
}
