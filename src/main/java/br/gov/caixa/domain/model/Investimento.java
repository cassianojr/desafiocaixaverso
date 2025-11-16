package br.gov.caixa.domain.model;

import java.time.LocalDate;

public record Investimento(
        Long id,
        Long clienteId,
        String tipo,
        Double valor,
        Double rentabilidade,
        LocalDate data
) {}
