package br.gov.caixa.domain.model;

import java.math.BigDecimal;

public record Simulacao(
        Long clienteId,
        BigDecimal valor,
        Integer prazoMeses,
        String tipoProduto
) {
}
