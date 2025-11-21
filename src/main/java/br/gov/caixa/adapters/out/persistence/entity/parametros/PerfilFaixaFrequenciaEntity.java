package br.gov.caixa.adapters.out.persistence.entity.parametros;
import jakarta.persistence.*;

@Entity
@Table(name = "PERFIL_FAIXA_FREQUENCIA")
public class PerfilFaixaFrequenciaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "QTD_MIN", nullable = false)
    private Integer qtdMin;

    @Column(name = "QTD_MAX")
    private Integer qtdMax;

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

    public Integer getQtdMin() {
        return qtdMin;
    }

    public void setQtdMin(Integer qtdMin) {
        this.qtdMin = qtdMin;
    }

    public Integer getQtdMax() {
        return qtdMax;
    }

    public void setQtdMax(Integer qtdMax) {
        this.qtdMax = qtdMax;
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