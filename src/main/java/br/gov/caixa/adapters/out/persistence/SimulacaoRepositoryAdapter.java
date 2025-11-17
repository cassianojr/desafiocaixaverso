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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class SimulacaoRepositoryAdapter implements SimulacaoRepository {

    public static final String QUERY_BUSCAR_SIMULACOES_POR_PRODUTO_E_DIA = """
        SELECT
            s.produto,
            CAST(s.dataSimulacao AS date),
            COUNT(s.id),
            AVG(s.valorFinal)
        FROM SimulacaoEntity s
        GROUP BY s.produto, CAST(s.dataSimulacao AS date)
    """;
    @Inject
    EntityManager entityManager;

    @Inject
    SimulacaoMapper simulacaoMapper;

    @Override
    @Transactional
    public void salvarSimulacao(Long aLong, String nome, BigDecimal valor, BigDecimal valorFinal, Integer integer, LocalDate localDateTime) {
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
                .map(row -> new SimulacaoPorProdutoDia(
                        (String) row[0],
                        LocalDate.parse(row[1].toString()),
                        ((Number) row[2]).longValue(),
                        ((Number) row[3]).doubleValue()
                )).toList();
    }
}
