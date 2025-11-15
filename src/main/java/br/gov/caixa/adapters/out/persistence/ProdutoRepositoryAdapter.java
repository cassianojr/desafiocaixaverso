package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.ProdutoEntity;
import br.gov.caixa.adapters.out.persistence.mapper.EntityModelMapper;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.port.out.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ProdutoRepositoryAdapter implements ProdutoRepository {

    @Inject
    EntityModelMapper mapper;

    @Inject
    EntityManager entityManager;

    @Override
    public List<Produto> findAll() {
        return mapper.mapAll(entityManager.createQuery("from ProdutoEntity", ProdutoEntity.class).getResultList(), Produto.class);
    }

    @Override
    public Optional<Produto> findByTipo(String tipo) {
        List<ProdutoEntity> result = entityManager.createQuery("from ProdutoEntity p where p.tipo = :tipo", ProdutoEntity.class)
                .setParameter("tipo", tipo)
                .getResultList();
        if (result.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(mapper.map(result.stream().findFirst(), Produto.class));
        }
    }

    @Override
    public List<Produto> findByRisco(String risco) {
        return entityManager.createQuery("FROM ProdutoEntity WHERE risco = :risco", ProdutoEntity.class)
                .setParameter("risco", risco)
                .getResultList()
                .stream()
                .map(entity -> mapper.map(entity, Produto.class))
                .toList();
    }
}
