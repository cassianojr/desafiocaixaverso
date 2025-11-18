package br.gov.caixa.infrastructure;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class TesteCobertura {

    public int soma(int a, int b){
        return a + b;
    }
}
