package br.gov.caixa.domain.port.out;


import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface SimulacaoRepository {
    void salvarSimulacao(Long aLong, String nome, BigDecimal valor, BigDecimal valorFinal, Integer integer, LocalDate dateTime);

    List<SimulacaoHistorico> listarTodas();

    List<SimulacaoPorProdutoDia> agruparPorProdutoEDia();
}
