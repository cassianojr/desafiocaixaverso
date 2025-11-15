package br.gov.caixa.domain.model;

import java.time.LocalDate;

public record Telemetria(
        Long id,
        String servico,
        Long quantidadeChamadas,
        Double mediaTempoRespostaMs,
        LocalDate data
) {}
