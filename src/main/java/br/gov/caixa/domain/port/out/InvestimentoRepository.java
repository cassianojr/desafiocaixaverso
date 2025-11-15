package br.gov.caixa.domain.port.out;

import br.gov.caixa.domain.model.Investimento;

import java.util.List;

public interface InvestimentoRepository {
    List<Investimento> listarPorCliente(Long clienteId);
}
