# Domínio

## Objetivo
Concentrar regras específicas de investimento, simulação, recomendação e perfil de risco de forma independente de frameworks.

## Principais Modelos
- `Simulacao`: entrada para cálculo (valor investido, prazo, rentabilidade anual, tipo produto).
- `SimulacaoResultado`: saída do caso de uso (valor final, rentabilidade efetiva, dados do produto utilizado, timestamp).
- `Produto`: características de rentabilidade e tipo.
- `PerfilRiscoResultado`: classificação (Conservador, Moderado, Agressivo) e pontuação detalhada.
- `TelemetriaDiaria`: consolida métricas por serviço.

## Use Cases (Ports de Entrada)
Localizados em `br.gov.caixa.domain.port.in`:
- `SimularInvestimentoUseCase`
- `ConsultarSimulacoesUseCase`
- `ConsultarSimulacoesPorProdutoDiaUseCase`
- `ConsultarInvestimentosUseCase`
- `ConsultarPerfilRiscoUseCase`
- `ConsultarProdutosRecomendadosUseCase`
- `ConsultarTelemetriaUseCase`

## Ports de Saída
Interfaces para persistência e consulta: `br.gov.caixa.domain.port.out` (quando presente). Fornecidas aos casos de uso via injeção (DI) pelos adapters.

## Regras Essenciais
- Simulação: fórmula composta mensal.
- Pontuação de perfil: soma de critérios discretos.
- Produtos: seleção pelo maior `rentabilidadeAnual` dentro do tipo solicitado.
- Telemetria: agregação diária acumulativa.

## Exceções
Exceções em `exception` sinalizam condições de negócio (ex.: produto não encontrado). São convertidas para respostas HTTP adequadas nos resources via mapeamento padrão Quarkus/JAX-RS ou handlers utilitários.

## Imutabilidade
Records e objetos de retorno evitam mutação após construção, favorecendo testes e previsibilidade.

## Testabilidade
Casos de uso podem ser testados com implementações fake dos ports de saída sem subir contexto Quarkus, acelerando feedback.
