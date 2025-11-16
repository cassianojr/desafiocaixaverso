package br.gov.caixa.domain.model;

import java.time.LocalDateTime;

public record Investimento(
        Long id,
        Long clienteId,
        String tipo,
        Double valor,
        Double rentabilidade,
        LocalDateTime data
) {}
