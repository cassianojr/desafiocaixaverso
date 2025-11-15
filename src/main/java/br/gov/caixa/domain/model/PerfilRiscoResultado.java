package br.gov.caixa.domain.model;

public record PerfilRiscoResultado(
        Long clienteId,
        String perfil,
        Integer pontuacao,
        String descricao
) { }
