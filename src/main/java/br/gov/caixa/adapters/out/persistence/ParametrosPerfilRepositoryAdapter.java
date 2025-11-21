package br.gov.caixa.adapters.out.persistence;

import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilFaixaFrequenciaEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilFaixaVolumeEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilInvestidorFaixaPontuacaoEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilPreferenciaLiquidezEntity;
import br.gov.caixa.domain.port.out.ParametrosPerfilRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@ApplicationScoped
public class ParametrosPerfilRepositoryAdapter implements ParametrosPerfilRepository {

    @Inject
    EntityManager em;

    @Override
    public Optional<PerfilFaixaVolumeEntity> encontrarFaixaPorVolume(BigDecimal volumeTotal) {
        List<PerfilFaixaVolumeEntity> result = em.createQuery(
                        "SELECT f FROM PerfilFaixaVolumeEntity f " +
                                "WHERE f.valorMin <= :valor " +
                                "AND (f.valorMax IS NULL OR f.valorMax >= :valor)",
                        PerfilFaixaVolumeEntity.class)
                .setParameter("valor", volumeTotal)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public Optional<PerfilFaixaFrequenciaEntity> encontrarFaixaPorFrequencia(int quantidadeInvestimentos) {
        List<PerfilFaixaFrequenciaEntity> result = em.createQuery(
                        "SELECT f FROM PerfilFaixaFrequenciaEntity f " +
                                "WHERE f.qtdMin <= :qtd " +
                                "AND (f.qtdMax IS NULL OR f.qtdMax >= :qtd)",
                        PerfilFaixaFrequenciaEntity.class)
                .setParameter("qtd", quantidadeInvestimentos)
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public Optional<PerfilPreferenciaLiquidezEntity> encontrarFaixaPorPreferencia(double percentualLiquidez) {
        List<PerfilPreferenciaLiquidezEntity> result = em.createQuery(
                        "SELECT f FROM PerfilPreferenciaLiquidezEntity f " +
                                "WHERE f.percentualMin <= :perc " +
                                "AND (f.percentualMax IS NULL OR f.percentualMax >= :perc)",
                        PerfilPreferenciaLiquidezEntity.class)
                .setParameter("perc", BigDecimal.valueOf(percentualLiquidez))
                .getResultList();

        return result.stream().findFirst();
    }

    @Override
    public Optional<PerfilInvestidorFaixaPontuacaoEntity> encontrarFaixaPorPontuacao(int pontuacaoFinal) {
        List<PerfilInvestidorFaixaPontuacaoEntity> result = em.createQuery(
                        "SELECT f FROM PerfilInvestidorFaixaPontuacaoEntity f " +
                                "WHERE f.pontosMin <= :pontos " +
                                "AND (f.pontosMax IS NULL OR f.pontosMax >= :pontos)",
                        PerfilInvestidorFaixaPontuacaoEntity.class)
                .setParameter("pontos", pontuacaoFinal)
                .getResultList();

        return result.stream().findFirst();
    }
}
