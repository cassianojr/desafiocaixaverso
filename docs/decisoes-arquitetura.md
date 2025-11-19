# Suposições & Decisões de Arquitetura

Documento registra suposições e decisões arquiteturais feitas para cobrir lacunas da especificação original e garantir previsibilidade.

## 1. Modelagem de Domínio
### 1.1 Cliente
A especificação referencia `clienteId`, mas não define entidade Cliente.

Decisão: o sistema não mantém entidade Cliente. `clienteId` é tratado como valor externo válido por definição.

## 2. Regras de Simulação de Investimento
A especificação não define fórmula, periodicidade nem imposto.

### 2.1 Tipo de cálculo
Assunção: rentabilidade informada via banco representa rentabilidade anual.

### 2.2 Fórmula aplicada
Decisão: cálculo composto mensal.

```
valorFinal = valorInvestido * (1 + rentabilidadeAnual / 12) ^ prazoMeses
```

### 2.3 Impostos
Decisão: IR/IOF não são aplicados (não foram definidos no desafio).

### 2.4 Rentabilidade efetiva retornada
```
rentabilidadeEfetiva = (valorFinal / valorInicial) - 1
```

## 3. Produtos de Investimento
A especificação não lista produtos nem regras específicas.

### 3.1 Produtos pré-carregados
Assunção: tabela `produtos` é populada via `import.sql` com produtos fictícios que cobrem os tipos solicitados no desafio.

### 3.2 Validação
Decisão: filtro por `tipoProduto` usando correspondência exata do campo `tipo`. Se mais de um produto estiver disponível, retorna o de maior rentabilidade.

## 4. Motor de Recomendação
A descrição do desafio descreve conceitos, mas não define as pontuações a serem utilizadas.

### 4.1 Fonte de dados
Assunção: preferências do cliente derivadas do histórico local de investimentos (`GET /investimentos/{clienteId}`).

### 4.2 Pontuação (scoring)
Critérios e pontos:

| Critério | Faixa | Pontos |
|----------|-------|--------|
| Volume total investido | < 5k | 10 |
| Volume total investido | 5k–20k | 20 |
| Volume total investido | > 20k | 30 |
| Frequência movimentações (mês) | < 2 | 10 |
| Frequência movimentações (mês) | 2–5 | 20 |
| Frequência movimentações (mês) | > 5 | 30 |
| Preferência (liquidez vs rentabilidade) | maior liquidez (LCI/CDB) | 10 |
| Preferência (liquidez vs rentabilidade) | maior rentabilidade (Fundos) | 30 |

Faixas de perfil:
- 0–40 → Conservador
- 41–70 → Moderado
- 71–100 → Agressivo

## 5. Telemetria
A especificação não define granularidade, armazenamento ou reset.

### 5.1 Armazenamento
Decisão: uma linha por dia e por serviço na tabela `telemetria`.

### 5.2 Dados armazenados
- `servico`
- `quantidadeChamadas`
- `mediaTempoRespostaMs`
- `data`

### 5.3 Período
Assunção: telemetria retorna o mês corrente.

## 6. Autenticação & Autorização
A especificação não define roles, escopos ou endpoints públicos.

### 6.1 Autenticação
Decisão: Keycloak + JWT via Quarkus OIDC.

### 6.2 Papéis
- `user`: pode simular, consultar simulações, consultar perfil, consultar produtos recomendados.
- `admin`: acesso adicional a `/telemetria`.

### 6.3 Endpoints públicos
Nenhum endpoint foi definido como público.

## 7. Persistência
### 7.1 Entidades
Entidades JPA não são iguais aos records do domínio. Em linha com arquitetura hexagonal, adapters/out definem suas entidades JPA.

### 7.2 Relacionamentos
Decisão: simulações não possuem FK para Produto. Produto é salvo inline (string) para manter independência do domínio.
