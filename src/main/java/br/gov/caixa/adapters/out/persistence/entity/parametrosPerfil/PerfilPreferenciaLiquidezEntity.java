package br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PERFIL_PREFERENCIA_LIQUIDEZ")
public class PerfilPreferenciaLiquidezEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PORCENTAGEM_MIN", nullable = false, precision = 5, scale = 2)
    private BigDecimal percentualMin;

    @Column(name = "PORCENTAGEM_MAX", precision = 5, scale = 2)
    private BigDecimal percentualMax;

    @Column(name = "PONTOS", nullable = false)
    private Integer pontos;

    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    public void setId(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public BigDecimal getPercentualMin() {
        return percentualMin;
    }

    public void setPercentualMin(BigDecimal percentualMin) {
        this.percentualMin = percentualMin;
    }

    public BigDecimal getPercentualMax() {
        return percentualMax;
    }

    public void setPercentualMax(BigDecimal percentualMax) {
        this.percentualMax = percentualMax;
    }

    public Integer getPontos() {
        return pontos;
    }

    public void setPontos(Integer pontos) {
        this.pontos = pontos;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
