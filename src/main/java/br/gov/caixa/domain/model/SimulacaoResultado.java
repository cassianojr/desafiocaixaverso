package br.gov.caixa.domain.model;

import java.time.LocalDate;

public record SimulacaoResultado(
        Produto produtoValidado,
        ResultadoSimulacao resultadoSimulacao,
        LocalDate dataSimulacao
) {}
