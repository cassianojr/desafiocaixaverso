package br.gov.caixa.domain.port.out;

import br.gov.caixa.domain.model.Telemetria;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TelemetriaRepository {
    void registrarChamada(String servico, double tempoRespostaMs, LocalDate data);
    List<Telemetria> listarPorPeriodo(LocalDate dataInicio, LocalDate dataFim);
}
