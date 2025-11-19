# Desafio Caixaverso

API construída em Quarkus demonstrando simulação de investimentos, recomendação de produtos e cálculo de perfil de risco usando arquitetura hexagonal (ports & adapters). Inclui autenticação via Keycloak, persistência em SQL Server e telemetria agregada diária.

## Análise do SONAR

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=bugs)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=code_smells)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=coverage)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Security Rating](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=security_rating)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=vulnerabilities)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)
[![Maintainability Rating](https://sonarcloud.io/api/project_badges/measure?project=cassianojr_caixaverso&metric=sqale_rating)](https://sonarcloud.io/summary/new_code?id=cassianojr_caixaverso)

## Sumário
1. Visão Geral
2. Como Executar (Docker e local)
3. Arquitetura Hexagonal (resumo)
4. Endpoints
5. Decisões e Suposições
6. Desenvolvimento e Build
7. Referências

## 1. Visão Geral
O domínio encapsula regras de simulação, seleção de produtos e classificação de perfil. Adapters de entrada expõem endpoints REST e adapters de saída persistem dados via JPA. Segurança baseada em roles (`user`, `admin`) controladas pelo Keycloak.

## 2. Como Executar
### 2.1 Via Docker Compose (recomendado)
Pré-requisitos: Docker instalado. Portas livres: 8080 (API), 8081 (Keycloak), 1433 (SQL Server).

```powershell
# (opcional) construir artefato local
./mvnw package -DskipTests

# subir tudo
docker compose up --build -d

# acompanhar logs
docker compose logs -f api
```

Parar e remover volumes: `docker compose down -v`

Detalhes completos: `docs/execucao-docker.md`.

### 2.2 Dev Mode Local (hot reload)
Suba apenas dependências externas:
```powershell
docker compose up -d keycloak sqlserver
./mvnw quarkus:dev
```
Dev UI: <http://localhost:8080/q/dev/>

## 3. Arquitetura Hexagonal (Resumo)
Camadas principais:
- Dominio: modelos, regras, interfaces (ports). Sem dependência de Quarkus.
- Aplicação: casos de uso em `application.service` orquestrando domínio e ports.
- Adapters In: `adapters.in.rest` expondo REST com JAX-RS.
- Adapters Out: `adapters.out.persistence` mapeando entidades JPA e conversões.
- Infraestrutura: configuração (OIDC, telemetria, segurança) em `infrastructure`.

Fluxo: Resource -> Use Case -> Port Out -> Persistência -> Retorno. Domínio permanece isolado de detalhes técnicos.

Documentação completa: `docs/arquitetura-hexagonal.md`.

## 4. Endpoints
| Método | Rota | Descrição | Roles |
|--------|------|-----------|-------|
| POST | /simular-investimento | Executa simulação | user, admin |
| GET | /simulacoes | Lista simulações | user, admin |
| GET | /simulacoes/por-produto-dia | Agrupamento simulações por produto/dia | user, admin |
| GET | /investimentos/{clienteId} | Histórico de investimentos cliente | user, admin |
| GET | /perfil-risco/{clienteId} | Calcula perfil de risco | user, admin |
| GET | /produtos-recomendados/{perfil} | Produtos para perfil informado | user, admin |
| GET | /telemetria | Métricas agregadas mês corrente | admin |

## 5. Decisões e Suposições
Resumo:
- Cliente não é entidade persistida; `clienteId` é referência externa.
- Fórmula de simulação: composição mensal com rentabilidade anual.
- Sem impostos (IR/IOF) por ausência de especificação.
- Seleção de produto: maior rentabilidade dentro do tipo solicitado.
- Perfil de risco: pontuação discreta por volume, frequência e preferência.
- Telemetria: linha diária por serviço, mês corrente.
- Segurança: Keycloak + JWT; somente `admin` acessa telemetria.
- Simulação guarda produto inline evitando FK.

Documento completo: `docs/decisoes-arquitetura.md`.

## 6. Estrutura de Pastas (alto nível)
```
src/main/java/br/gov/caixa/
├── domain
├── application/service
├── adapters/in/rest
├── adapters/out/persistence
└── infrastructure
```

## 7. Documentação Complementar
Consulte pasta `docs/`:
- `arquitetura-hexagonal.md`
- `execucao-docker.md`
- `decisoes-arquitetura.md`
- `dominio.md`

