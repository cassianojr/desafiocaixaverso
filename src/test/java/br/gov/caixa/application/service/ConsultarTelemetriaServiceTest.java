package br.gov.caixa.application.service;

import br.gov.caixa.domain.model.PeriodoTelemetria;
import br.gov.caixa.domain.model.Telemetria;
import br.gov.caixa.domain.model.TelemetriaResultado;
import br.gov.caixa.domain.port.out.TelemetriaRepository;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ConsultarTelemetriaServiceTest {

    private ConsultarTelemetriaService service;
    private TelemetriaRepository telemetriaRepository;

    @BeforeEach
    void setUp() {
        service = new ConsultarTelemetriaService();
        telemetriaRepository = mock(TelemetriaRepository.class);
        service.telemetriaRepository = telemetriaRepository; // mesmo package
    }

    private Telemetria tel(long id, String servico, long qtd, double media, LocalDate data) {
        return new Telemetria(id, servico, qtd, media, data);
    }

    @Test
    @DisplayName("Deve consultar telemetria do mês corrente e retornar período correto")
    void consultarSucesso() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioEsperado = hoje.withDayOfMonth(1);
        List<Telemetria> lista = List.of(
                tel(1,"/investimentos", 12, 150.5, hoje.minusDays(1)),
                tel(2,"/simulacoes", 7, 98.0, hoje)
        );
        when(telemetriaRepository.listarPorPeriodo(inicioEsperado, hoje)).thenReturn(lista);

        TelemetriaResultado resultado = service.consultar();

        assertNotNull(resultado);
        assertEquals(2, resultado.servicos().size());
        PeriodoTelemetria periodo = resultado.periodo();
        assertEquals(inicioEsperado, periodo.inicio());
        assertEquals(hoje, periodo.fim());
        verify(telemetriaRepository).listarPorPeriodo(inicioEsperado, hoje);
    }

    @Test
    @DisplayName("Lista vazia deve retornar período válido e lista sem elementos")
    void consultarListaVazia() {
        LocalDate hoje = LocalDate.now();
        LocalDate inicioEsperado = hoje.withDayOfMonth(1);
        when(telemetriaRepository.listarPorPeriodo(inicioEsperado, hoje)).thenReturn(List.of());

        TelemetriaResultado resultado = service.consultar();

        assertNotNull(resultado);
        assertTrue(resultado.servicos().isEmpty());
        assertEquals(inicioEsperado, resultado.periodo().inicio());
        assertEquals(hoje, resultado.periodo().fim());
        verify(telemetriaRepository).listarPorPeriodo(inicioEsperado, hoje);
    }
}
