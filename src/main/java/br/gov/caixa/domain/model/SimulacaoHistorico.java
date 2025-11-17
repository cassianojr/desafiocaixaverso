package br.gov.caixa.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record SimulacaoHistorico(
        Long id,
        Long clienteId,
        String produto,
        BigDecimal valorInvestido,
        BigDecimal valorFinal,
        Integer prazoMeses,
        LocalDate dataSimulacao
) {
}
