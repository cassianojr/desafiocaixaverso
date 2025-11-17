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

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDate;

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

        MathContext mc = MathContext.DECIMAL64;

        // rentabilidade anual → BigDecimal
        BigDecimal valorFinal = calcularValorFinal(simulacao, produto, mc);

        // rentabilidade efetiva = (valorFinal / valorInicial) - 1
        BigDecimal rentabilidadeEfetiva = valorFinal
                .divide(simulacao.valor(), mc)
                .subtract(BigDecimal.ONE, mc);

        // montar DTOs
        ResultadoSimulacao resultadoSimulacao = new ResultadoSimulacao(
                //arredonda para 2 casas decimais
                valorFinal.setScale(2, RoundingMode.HALF_EVEN),
                rentabilidadeEfetiva.setScale(2, RoundingMode.HALF_EVEN),
                simulacao.prazoMeses()
        );

        SimulacaoResultado simulacaoResultado = new SimulacaoResultado(
                produto,
                resultadoSimulacao,
                LocalDate.now()
        );

        // salvar entidade
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

    private static BigDecimal calcularValorFinal(Simulacao simulacao, Produto produto, MathContext mc) {
        BigDecimal rentabilidadeAnual = produto.rentabilidade(); // ex: 0.12

        // taxa mensal = anual / 12
        BigDecimal taxaMensal = rentabilidadeAnual
                .divide(BigDecimal.valueOf(12), mc);

        // (1 + taxaMensal)
        BigDecimal fatorMensal = BigDecimal.ONE.add(taxaMensal, mc);

        // potencia: (1 + taxaMensal) ^ prazoMeses
        BigDecimal fatorAcumulado = fatorMensal.pow(simulacao.prazoMeses(), mc);

        // valorFinal = valorInicial * fator
        BigDecimal valorFinal = simulacao.valor()
                .multiply(fatorAcumulado, mc);
        return valorFinal;
    }
}
