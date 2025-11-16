package br.gov.caixa.domain.model;

import java.time.LocalDateTime;

public record TelemetriaAgrupada(
        String servico,
        Long quantidadeChamadas,
        Double mediaTempoRespostaMs,
        LocalDateTime ultimoAcesso
) {}
