package br.gov.caixa.domain.port.out;

import br.gov.caixa.domain.model.Produto;

import java.util.List;
import java.util.Optional;

public interface ProdutoRepository {

    List<Produto> findAll();
    Optional<Produto> findByTipo(String tipo);
    List<Produto> findByRisco(String risco);
}
