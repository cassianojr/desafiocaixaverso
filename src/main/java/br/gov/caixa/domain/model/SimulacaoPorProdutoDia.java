package br.gov.caixa.domain.model;

import java.time.LocalDate;

public record SimulacaoPorProdutoDia(
        String produto,
        LocalDate data,
        Long quantidadeSimulacoes,
        Double mediaValorFinal
) {}
