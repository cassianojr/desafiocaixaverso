package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.PeriodoTelemetria;
import br.gov.caixa.domain.model.Telemetria;
import br.gov.caixa.domain.model.TelemetriaResultado;
import br.gov.caixa.domain.port.in.ConsultarTelemetriaUseCase;
import br.gov.caixa.domain.port.out.TelemetriaRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.time.LocalDate;
import java.util.List;

@ApplicationScoped
public class ConsultarTelemetriaService implements ConsultarTelemetriaUseCase {

    @Inject
    TelemetriaRepository telemetriaRepository;

    @Override
    public TelemetriaResultado consultar() {
        LocalDate dataFim = LocalDate.now();
        LocalDate dataInicio = dataFim.withDayOfMonth(1);

        List<Telemetria> servicos = telemetriaRepository.listarPorPeriodo(dataInicio, dataFim);
        PeriodoTelemetria periodo = new PeriodoTelemetria(dataInicio, dataFim);
        return new TelemetriaResultado(servicos, periodo);
    }
}
