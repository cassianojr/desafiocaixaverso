package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.SimulacaoEntity;
import br.gov.caixa.adapters.out.persistence.mapper.SimulacaoMapper;
import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@ApplicationScoped
public class SimulacaoRepositoryAdapter implements SimulacaoRepository {

    public static final String QUERY_BUSCAR_SIMULACOES_POR_PRODUTO_E_DIA = """
                SELECT
                    s.produto,
                    DATE(s.dataSimulacao),
                    COUNT(s.id),
                    AVG(s.valorFinal)
                FROM SimulacaoEntity s
                GROUP BY s.produto, DATE(s.dataSimulacao)
            """;
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

    @Override
    public List<SimulacaoPorProdutoDia> agruparPorProdutoEDia() {
        List<Object[]> result = entityManager.createQuery(QUERY_BUSCAR_SIMULACOES_POR_PRODUTO_E_DIA, Object[].class).getResultList();

        return result.stream()
                .map(row-> new SimulacaoPorProdutoDia(
                        (String) row[0],
                        java.time.LocalDateTime.parse(row[1].toString()),
                        (Long) row[2],
                        ((Double) row[3])
                )).toList();
    }
}
