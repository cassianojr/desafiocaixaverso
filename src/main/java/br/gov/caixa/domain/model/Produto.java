package br.gov.caixa.domain.model;

public record Produto(Long id, String nome, String tipo, Double rentabilidade, String risco) {
}
