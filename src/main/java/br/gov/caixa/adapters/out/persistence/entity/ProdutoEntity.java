package br.gov.caixa.adapters.out.persistence.entity;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFilter;

import java.math.BigDecimal;

@Entity
@Table(name="produto")
@JsonFilter(br.gov.caixa.infrastructure.config.JacksonConfig.FILTER_NOT_INITIALIZED_FIELDS)
public class ProdutoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private String tipo;
    private BigDecimal rentabilidade;
    private String risco;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getRentabilidade() {
        return rentabilidade;
    }

    public void setRentabilidade(BigDecimal rentabilidade) {
        this.rentabilidade = rentabilidade;
    }

    public String getRisco() {
        return risco;
    }

    public void setRisco(String risco) {
        this.risco = risco;
    }
}
