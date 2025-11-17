package br.gov.caixa.domain.model;

import java.math.BigDecimal;

public record ResultadoSimulacao(
        BigDecimal valorFinal,
        BigDecimal rentabilidadeEfetiva,
        Integer prazoMeses
) {
}
