package br.gov.caixa.domain.model;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Investimento(
        Long id,
        Long clienteId,
        String tipo,
        BigDecimal valor,
        BigDecimal rentabilidade,
        LocalDate data
) {}
