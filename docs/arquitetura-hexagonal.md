# Arquitetura Hexagonal

Este projeto segue arquitetura hexagonal (ports & adapters) para isolar o domínio de detalhes de infraestrutura.

## Visão Geral das Camadas

- Dominio (`br.gov.caixa.domain`): regras de negócio puras. Contém `model`, `enums`, `exception` e `port`.
  - `model`: records e classes imutáveis que representam conceitos (Simulacao, Produto, PerfilRiscoResultado etc.).
  - `enums`: classificações de domínio (tipos de investimento, perfis, etc.).
  - `exception`: exceções de negócio controladas para sinalizar erros previstos.
  - `port`: interfaces que definem contratos de entrada (use cases) e saída (acesso a repositórios / serviços externos).
- Aplicação (`br.gov.caixa.application.service`): implementação dos casos de uso. Orquestra o domínio, aplica regras e chama ports de saída. Não conhece detalhes técnicos de persistência ou transporte.
- Adapters de Entrada (`br.gov.caixa.adapters.in.rest`): recursos REST que expõem endpoints HTTP. Cada resource injeta um use case (port in) e converte request/response JSON.<br>
  Endpoints:
  - `POST /simular-investimento` (user, admin)
  - `GET /simulacoes` (user, admin)
  - `GET /simulacoes/por-produto-dia` (user, admin)
  - `GET /investimentos/{clienteId}` (user, admin)
  - `GET /perfil-risco/{clienteId}` (user, admin)
  - `GET /produtos-recomendados/{perfil}` (user, admin)
  - `GET /telemetria` (admin)
- Adapters de Saída (`br.gov.caixa.adapters.out.persistence`): implementação dos ports de saída usando JPA/Hibernate Panache. Mapeia entidades persistentes distintas dos records do domínio. Inclui `entity` e `mapper` para conversões.
- Infraestrutura (`br.gov.caixa.infrastructure`): configurações transversais (security OIDC/Keycloak, telemetria, propriedades). Não contém regra de negócio.

## Fluxo de Chamada
1. Resource REST recebe requisição e valida/transforma dados básicos.
2. Injeta e chama o caso de uso (port in).
3. Caso de uso executa lógica, consulta/adiciona dados via ports de saída.
4. Adaptador de saída faz persistência ou consulta e retorna modelos de domínio.
5. Resource devolve resposta JSON.

## Isolamento do Domínio
- O domínio nunca depende de classes de framework (Jakarta, Quarkus). Apenas tipos próprios e Java padrão.
- Entidades JPA não "vazam" para o domínio. Conversões ocorrem nos adapters.

## Decisões Importantes
- Simulação usa fórmula composta mensal (detalhada em `decisoes-arquitetura.md`).
- Produto é salvo inline na simulação (string) evitando acoplamento via FK.
- Perfil de risco é derivado de pontuação calculada sobre histórico de investimentos.

## Benefícios
- Testes unitários podem instanciar casos de uso com stubs/fakes sem subir servidor.
- Troca de persistência ou transporte (REST para mensageria) exige apenas novos adapters.
- Regras de negócio evoluem sem impacto em infraestrutura.

## Extensões Futuras
- Adicionar adapter de saída para cache.
- Expor métricas Prometheus com novo adapter de entrada.
- Implementar notificação assíncrona (ex.: evento após simulação) sem alterar domínio.
