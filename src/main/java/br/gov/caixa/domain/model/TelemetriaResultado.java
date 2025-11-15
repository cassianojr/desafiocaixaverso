package br.gov.caixa.domain.model;

import java.util.List;

public record TelemetriaResultado(
        List<Telemetria> servicos,
        PeriodoTelemetria periodo
) {
}
