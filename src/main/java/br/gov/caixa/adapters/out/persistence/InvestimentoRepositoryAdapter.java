package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.InvestimentoEntity;
import br.gov.caixa.adapters.out.persistence.mapper.EntityModelMapper;
import br.gov.caixa.domain.model.Investimento;
import br.gov.caixa.domain.port.out.InvestimentoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;

@ApplicationScoped
public class InvestimentoRepositoryAdapter implements InvestimentoRepository {

    @Inject
    EntityManager em;

    @Inject
    EntityModelMapper mapper;

    @Override
    public List<Investimento> listarPorCliente(Long clienteId) {
        return em.createQuery("""
            FROM InvestimentoEntity i
            WHERE i.clienteId = :clienteId
            """, InvestimentoEntity.class)
                .setParameter("clienteId", clienteId)
                .getResultList()
                .stream()
                .map(e->mapper.map(e, Investimento.class)).toList();

    }
}
