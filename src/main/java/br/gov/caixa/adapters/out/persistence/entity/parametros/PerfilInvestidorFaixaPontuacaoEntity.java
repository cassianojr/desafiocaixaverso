package br.gov.caixa.adapters.out.persistence.entity.parametros;

import jakarta.persistence.*;

@Entity
@Table(name = "PERFIL_INVESTIDOR_FAIXA_PONTUACAO")
public class PerfilInvestidorFaixaPontuacaoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "PONTOS_MIN", nullable = false)
    private Integer pontosMin;

    @Column(name = "PONTOS_MAX")
    private Integer pontosMax;

    @Column(name = "PERFIL", nullable = false, length = 20)
    private String perfil;

    @Column(name = "DESCRICAO", length = 200)
    private String descricao;

    public void setId(Long id){
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Integer getPontosMin() {
        return pontosMin;
    }

    public void setPontosMin(Integer pontosMin) {
        this.pontosMin = pontosMin;
    }

    public Integer getPontosMax() {
        return pontosMax;
    }

    public void setPontosMax(Integer pontosMax) {
        this.pontosMax = pontosMax;
    }

    public String getPerfil() {
        return perfil;
    }

    public void setPerfil(String perfil) {
        this.perfil = perfil;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
}
