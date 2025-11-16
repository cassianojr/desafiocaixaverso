package br.gov.caixa.domain.model;

import java.time.LocalDateTime;

public record Telemetria(
        Long id,
        String servico,
        Long quantidadeChamadas,
        Double mediaTempoRespostaMs,
        LocalDateTime data
) {}
