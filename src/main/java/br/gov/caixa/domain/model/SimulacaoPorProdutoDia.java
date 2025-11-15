package br.gov.caixa.domain.model;

import java.time.LocalDateTime;

public record SimulacaoPorProdutoDia(
        String produto,
        LocalDateTime data,
        Long quantidadeSimulacoes,
        Double mediaValorFinal
) {}
