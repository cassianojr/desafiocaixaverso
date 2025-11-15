package br.gov.caixa.domain.model;

import java.time.LocalDateTime;

public record SimulacaoHistorico(
        Long id,
        Long clienteId,
        String produto,
        Double valorInvestido,
        Double valorFinal,
        Integer prazoMeses,
        LocalDateTime dataSimulacao
) {
}
