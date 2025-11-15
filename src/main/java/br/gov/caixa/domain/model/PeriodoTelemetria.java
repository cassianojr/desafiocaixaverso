package br.gov.caixa.domain.model;

import java.time.LocalDate;

public record PeriodoTelemetria(
        LocalDate inicio,
        LocalDate fim
) {}
