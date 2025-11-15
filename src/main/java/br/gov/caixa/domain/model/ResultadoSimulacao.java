package br.gov.caixa.domain.model;

public record ResultadoSimulacao(
        Double valorFinal,
        Double rentabilidadeEfetiva,
        Integer prazoMeses
) {
}
