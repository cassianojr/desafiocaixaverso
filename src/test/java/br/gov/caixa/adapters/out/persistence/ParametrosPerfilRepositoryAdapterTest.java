package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilFaixaFrequenciaEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilFaixaVolumeEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilInvestidorFaixaPontuacaoEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametros.PerfilPreferenciaLiquidezEntity;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@QuarkusTest
class ParametrosPerfilRepositoryAdapterTest {

    private ParametrosPerfilRepositoryAdapter adapter;
    private EntityManager em;

    @BeforeEach
    void setUp() {
        em = mock(EntityManager.class);
        adapter = new ParametrosPerfilRepositoryAdapter();
        adapter.em = em; // since it's package-private or we can use reflection, but for test, assume accessible
    }

    @Test
    @DisplayName("Deve encontrar faixa de volume quando existe")
    void encontrarFaixaPorVolumeExistente() {
        BigDecimal volume = BigDecimal.valueOf(5000);
        PerfilFaixaVolumeEntity entity = mock(PerfilFaixaVolumeEntity.class);
        TypedQuery<PerfilFaixaVolumeEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilFaixaVolumeEntity.class))).thenReturn(query);
        when(query.setParameter("valor", volume)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(entity));

        Optional<PerfilFaixaVolumeEntity> result = adapter.encontrarFaixaPorVolume(volume);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(query).setParameter("valor", volume);
    }

    @Test
    @DisplayName("Deve retornar empty quando não encontra faixa de volume")
    void encontrarFaixaPorVolumeNaoEncontrada() {
        BigDecimal volume = BigDecimal.valueOf(100000);
        TypedQuery<PerfilFaixaVolumeEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilFaixaVolumeEntity.class))).thenReturn(query);
        when(query.setParameter("valor", volume)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        Optional<PerfilFaixaVolumeEntity> result = adapter.encontrarFaixaPorVolume(volume);

        assertFalse(result.isPresent());
        verify(query).setParameter("valor", volume);
    }

    @Test
    @DisplayName("Deve encontrar faixa de frequência quando existe")
    void encontrarFaixaPorFrequenciaExistente() {
        int qtd = 3;
        PerfilFaixaFrequenciaEntity entity = mock(PerfilFaixaFrequenciaEntity.class);
        TypedQuery<PerfilFaixaFrequenciaEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilFaixaFrequenciaEntity.class))).thenReturn(query);
        when(query.setParameter("qtd", qtd)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(entity));

        Optional<PerfilFaixaFrequenciaEntity> result = adapter.encontrarFaixaPorFrequencia(qtd);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(query).setParameter("qtd", qtd);
    }

    @Test
    @DisplayName("Deve retornar empty quando não encontra faixa de frequência")
    void encontrarFaixaPorFrequenciaNaoEncontrada() {
        int qtd = 10;
        TypedQuery<PerfilFaixaFrequenciaEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilFaixaFrequenciaEntity.class))).thenReturn(query);
        when(query.setParameter("qtd", qtd)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        Optional<PerfilFaixaFrequenciaEntity> result = adapter.encontrarFaixaPorFrequencia(qtd);

        assertFalse(result.isPresent());
        verify(query).setParameter("qtd", qtd);
    }

    @Test
    @DisplayName("Deve encontrar faixa de preferência quando existe")
    void encontrarFaixaPorPreferenciaExistente() {
        double perc = 66.67;
        PerfilPreferenciaLiquidezEntity entity = mock(PerfilPreferenciaLiquidezEntity.class);
        TypedQuery<PerfilPreferenciaLiquidezEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilPreferenciaLiquidezEntity.class))).thenReturn(query);
        when(query.setParameter("perc", BigDecimal.valueOf(perc))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(entity));

        Optional<PerfilPreferenciaLiquidezEntity> result = adapter.encontrarFaixaPorPreferencia(perc);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(query).setParameter("perc", BigDecimal.valueOf(perc));
    }

    @Test
    @DisplayName("Deve retornar empty quando não encontra faixa de preferência")
    void encontrarFaixaPorPreferenciaNaoEncontrada() {
        double perc = 30.0;
        TypedQuery<PerfilPreferenciaLiquidezEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilPreferenciaLiquidezEntity.class))).thenReturn(query);
        when(query.setParameter("perc", BigDecimal.valueOf(perc))).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        Optional<PerfilPreferenciaLiquidezEntity> result = adapter.encontrarFaixaPorPreferencia(perc);

        assertFalse(result.isPresent());
        verify(query).setParameter("perc", BigDecimal.valueOf(perc));
    }

    @Test
    @DisplayName("Deve encontrar faixa de pontuação quando existe")
    void encontrarFaixaPorPontuacaoExistente() {
        int pontos = 50;
        PerfilInvestidorFaixaPontuacaoEntity entity = mock(PerfilInvestidorFaixaPontuacaoEntity.class);
        TypedQuery<PerfilInvestidorFaixaPontuacaoEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilInvestidorFaixaPontuacaoEntity.class))).thenReturn(query);
        when(query.setParameter("pontos", pontos)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of(entity));

        Optional<PerfilInvestidorFaixaPontuacaoEntity> result = adapter.encontrarFaixaPorPontuacao(pontos);

        assertTrue(result.isPresent());
        assertEquals(entity, result.get());
        verify(query).setParameter("pontos", pontos);
    }

    @Test
    @DisplayName("Deve retornar empty quando não encontra faixa de pontuação")
    void encontrarFaixaPorPontuacaoNaoEncontrada() {
        int pontos = 100;
        TypedQuery<PerfilInvestidorFaixaPontuacaoEntity> query = mock(TypedQuery.class);

        when(em.createQuery(anyString(), eq(PerfilInvestidorFaixaPontuacaoEntity.class))).thenReturn(query);
        when(query.setParameter("pontos", pontos)).thenReturn(query);
        when(query.getResultList()).thenReturn(List.of());

        Optional<PerfilInvestidorFaixaPontuacaoEntity> result = adapter.encontrarFaixaPorPontuacao(pontos);

        assertFalse(result.isPresent());
        verify(query).setParameter("pontos", pontos);
    }
}