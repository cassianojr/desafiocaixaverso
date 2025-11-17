package br.gov.caixa.domain.model;

import java.math.BigDecimal;

public record Produto(
        Long id,
        String nome,
        String tipo,
        BigDecimal rentabilidade,
        String risco
) {}
