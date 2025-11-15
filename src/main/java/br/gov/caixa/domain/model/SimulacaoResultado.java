package br.gov.caixa.domain.model;

import java.time.LocalDateTime;

public record SimulacaoResultado(
        Produto produtoValidado,
        ResultadoSimulacao resultadoSimulacao,
        LocalDateTime dataSimulacao
) {}
