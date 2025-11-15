package br.gov.caixa.application.service;

import br.gov.caixa.domain.exception.NegocioException;
import br.gov.caixa.domain.model.Produto;
import br.gov.caixa.domain.model.ResultadoSimulacao;
import br.gov.caixa.domain.model.Simulacao;
import br.gov.caixa.domain.model.SimulacaoResultado;
import br.gov.caixa.domain.port.in.SimularInvestimentoUseCase;
import br.gov.caixa.domain.port.out.ProdutoRepository;
import br.gov.caixa.domain.port.out.SimulacaoRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.jboss.resteasy.reactive.RestResponse;

import java.time.LocalDateTime;

@ApplicationScoped
public class SimulacaoService implements SimularInvestimentoUseCase {

    @Inject
    ProdutoRepository produtoRepository;

    @Inject
    SimulacaoRepository simulacaoRepository;

    @Override
    public SimulacaoResultado executar(Simulacao simulacao) {
        Produto produto = produtoRepository.findByTipo(simulacao.tipoProduto())
                .orElseThrow(() -> new NegocioException(RestResponse.StatusCode.NOT_FOUND, "Produto não encontrado"));

        double taxaMensal = produto.rentabilidade() / 12;
        double valorFinal = simulacao.valor() * Math.pow(1+taxaMensal, simulacao.prazoMeses());

        double rentabilidadeEfetiva = (valorFinal / simulacao.valor()) -1;

        ResultadoSimulacao resultadoSimulacao = new ResultadoSimulacao(
                valorFinal,
                rentabilidadeEfetiva,
                simulacao.prazoMeses()
        );
        SimulacaoResultado simulacaoResultado = new SimulacaoResultado(
                produto,
                resultadoSimulacao,
                LocalDateTime.now()
        );

        simulacaoRepository.salvarSimulacao(
                simulacao.clienteId(),
                produto.nome(),
                simulacao.valor(),
                valorFinal,
                simulacao.prazoMeses(),
                simulacaoResultado.dataSimulacao()
        );

        return simulacaoResultado;
    }
}
