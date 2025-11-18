package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.TelemetriaEntity;
import br.gov.caixa.adapters.out.persistence.mapper.EntityModelMapper;
import br.gov.caixa.domain.model.Telemetria;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@QuarkusTest
@TestInstance(Lifecycle.PER_CLASS)
class TelemetriaRepositoryAdapterTest {

    @Inject
    TelemetriaRepositoryAdapter adapter;

    @Inject
    EntityManager em;

    @Inject
    EntityModelMapper mapper;

    @Test
    @DisplayName("Registrar primeira chamada deve criar registro com qtd=1 e media=tempo")
    void registrarPrimeiraChamada() {
        LocalDate hoje = LocalDate.now();
        adapter.registrarChamada("/investimentos", 100.0, hoje);

        List<TelemetriaEntity> result = em.createQuery("FROM TelemetriaEntity WHERE servico = :s", TelemetriaEntity.class)
                .setParameter("s", "/investimentos")
                .getResultList();
        assertEquals(1, result.size());
        TelemetriaEntity e = result.getFirst();
        assertEquals(1L, e.getQuantidadeChamadas());
        assertEquals(100.0, e.getMediaTempoRespostaMs());
        assertEquals(hoje, e.getData());
    }

    @Test
    @DisplayName("Registrar múltiplas chamadas no mesmo dia atualiza média e quantidade")
    void registrarMultiplasChamadasMesmoDia() {
        LocalDate hoje = LocalDate.now();
        adapter.registrarChamada("/simulacoes", 50.0, hoje);
        adapter.registrarChamada("/simulacoes", 150.0, hoje);
        adapter.registrarChamada("/simulacoes", 100.0, hoje);

        TelemetriaEntity e = em.createQuery("FROM TelemetriaEntity WHERE servico = :s", TelemetriaEntity.class)
                .setParameter("s", "/simulacoes")
                .getSingleResult();
        assertEquals(3L, e.getQuantidadeChamadas());
        double expectedMedia = (50.0 + 150.0 + 100.0) / 3.0;
        assertEquals(expectedMedia, e.getMediaTempoRespostaMs(), 0.0001);
    }

    @Test
    @DisplayName("Registrar em dias diferentes deve criar registros distintos")
    void registrarDiasDiferentes() {
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);
        adapter.registrarChamada("/telemetria", 80.0, ontem);
        adapter.registrarChamada("/telemetria", 120.0, hoje);

        List<TelemetriaEntity> lista = em.createQuery("FROM TelemetriaEntity WHERE servico = :s ORDER BY data", TelemetriaEntity.class)
                .setParameter("s", "/telemetria")
                .getResultList();
        assertEquals(2, lista.size());
        assertEquals(ontem, lista.get(0).getData());
        assertEquals(hoje, lista.get(1).getData());
    }

    @Test
    @DisplayName("Listar por período deve retornar apenas registros dentro do intervalo inclusive")
    void listarPorPeriodo() {
        LocalDate hoje = LocalDate.now();
        LocalDate d1 = hoje.minusDays(4);
        LocalDate d2 = hoje.minusDays(2);
        LocalDate dFora = hoje.minusDays(10);

        adapter.registrarChamada("/filtro", 40.0, dFora); // fora
        adapter.registrarChamada("/filtro", 60.0, d1);    // dentro
        adapter.registrarChamada("/filtro", 80.0, d2);    // dentro

        List<Telemetria> periodo = adapter.listarPorPeriodo(d1.minusDays(1), hoje);

        assertEquals(2, periodo.size());
        assertTrue(periodo.stream().allMatch(t -> !t.data().equals(dFora)));
        assertEquals(d2, periodo.getFirst().data()); // ordenado desc -> primeira deve ser mais recente
    }
}
