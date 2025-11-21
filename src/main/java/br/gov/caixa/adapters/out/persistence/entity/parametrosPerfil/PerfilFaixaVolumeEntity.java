package br.gov.caixa.adapters.out.persistence.entity.parametrosPerfil;
import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "PERFIL_FAIXA_VOLUME")
public class PerfilFaixaVolumeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "VALOR_MIN", nullable = false, precision = 18, scale = 2)
    private BigDecimal valorMin;

    @Column(name = "VALOR_MAX", precision = 18, scale = 2)
    private BigDecimal valorMax;

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

    public BigDecimal getValorMin() {
        return valorMin;
    }

    public void setValorMin(BigDecimal valorMin) {
        this.valorMin = valorMin;
    }

    public BigDecimal getValorMax() {
        return valorMax;
    }

    public void setValorMax(BigDecimal valorMax) {
        this.valorMax = valorMax;
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
