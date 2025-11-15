package br.gov.caixa.domain.port.in;

import br.gov.caixa.domain.model.Produto;

import java.util.List;

public interface ConsultarProdutosRecomendadosUseCase {
    List<Produto> consultar(String perfil);
}
