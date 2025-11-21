package br.gov.caixa.domain.port.out;


import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilFaixaFrequenciaEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilFaixaVolumeEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilInvestidorFaixaPontuacaoEntity;
import br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil.PerfilPreferenciaLiquidezEntity;

import java.math.BigDecimal;
import java.util.Optional;

public interface ParametrosPerfilRepository {

    /**
     * Retorna a faixa de volume correspondente ao volume total investido.
     */
    Optional<PerfilFaixaVolumeEntity> encontrarFaixaPorVolume(BigDecimal volumeTotal);

    /**
     * Retorna a faixa de frequência de acordo com a quantidade de investimentos.
     */
    Optional<PerfilFaixaFrequenciaEntity> encontrarFaixaPorFrequencia(int quantidadeInvestimentos);

    /**
     * Retorna a pontuação de preferência baseado no percentual de produtos de alta liquidez.
     */
    Optional<PerfilPreferenciaLiquidezEntity> encontrarFaixaPorPreferencia(double percentualLiquidez);

    /**
     * Retorna o perfil do investidor baseado na pontuação final.
     */
    Optional<PerfilInvestidorFaixaPontuacaoEntity> encontrarFaixaPorPontuacao(int pontuacaoFinal);
}

