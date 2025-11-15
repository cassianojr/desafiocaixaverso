package br.gov.caixa.adapters.out.persistence.entity;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name="investimento")
public class InvestimentoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long clienteId;
    private String tipo;
    private Double valor;
    private Double rentabilidae;
    private LocalDate data;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }

    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Double getValor() {
        return valor;
    }

    public void setValor(Double valor) {
        this.valor = valor;
    }

    public Double getRentabilidae() {
        return rentabilidae;
    }

    public void setRentabilidae(Double rentabilidae) {
        this.rentabilidae = rentabilidae;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
