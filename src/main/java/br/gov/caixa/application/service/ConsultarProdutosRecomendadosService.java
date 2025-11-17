package br.gov.caixa.application.service;

import br.gov.caixa.domain.enums.PerfilInvestidor;
import br.gov.caixa.domain.enums.Risco;
import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.port.in.ConsultarProdutosRecomendadosUseCase;
import br.gov.caixa.domain.port.out.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

import java.util.List;

@ApplicationScoped
public class ConsultarProdutosRecomendadosService implements ConsultarProdutosRecomendadosUseCase {

    @Inject
    ProdutoRepository produtoRepository;

    @Override
    public List<Produto> consultar(String perfil) {

        if(perfil == null || perfil.isEmpty() || !PerfilInvestidor.isValid(perfil)){
            throw new NegocioException(RestResponse.StatusCode.BAD_REQUEST, "Perfil do investidor inválido");
        }

        PerfilInvestidor perfilinvestidor = PerfilInvestidor.fromString(perfil);
        Risco risco = perfilinvestidor.riscoPadrao();
        return produtoRepository.findByRisco(risco.name());
    }

}
