package br.gov.caixa.domain.port.out;


import br.gov.caixa.domain.model.SimulacaoHistorico;
import br.gov.caixa.domain.model.SimulacaoPorProdutoDia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface SimulacaoRepository {
    void salvarSimulacao(Long aLong, String nome, Double valor, double valorFinal, Integer integer, LocalDate dateTime);

    List<SimulacaoHistorico> listarTodas();

    List<SimulacaoPorProdutoDia> agruparPorProdutoEDia();
}
