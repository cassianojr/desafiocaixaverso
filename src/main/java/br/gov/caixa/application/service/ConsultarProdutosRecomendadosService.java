package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.port.in.ConsultarProdutosRecomendadosUseCase;
import br.gov.caixa.domain.port.out.ProdutoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;

@ApplicationScoped
public class ConsultarProdutosRecomendadosService implements ConsultarProdutosRecomendadosUseCase {

    @Inject
    ProdutoRepository produtoRepository;

    @Override
    public List<Produto> consultar(String perfil) {
        String risco;

        // Mapear perfil para risco
        switch(perfil.toLowerCase()){
            case "conservador" -> risco="Baixo";
            case "moderado" -> risco="Médio";
            case "agressivo" -> risco="Alto";
            default -> risco="Médio"; // padrão
        }

        return produtoRepository.findByRisco(risco);
    }

}
