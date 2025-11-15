package br.gov.caixa.domain.model;

public record Simulacao(
        Long clienteId,
        Double valor,
        Integer prazoMeses,
        String tipoProduto
) {
}
