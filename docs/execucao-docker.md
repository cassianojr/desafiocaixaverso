# Execução via Docker

Este projeto disponibiliza `docker-compose.yml` para subir Keycloak, SQL Server e a API.

## Pré-requisitos
- Docker e Docker Compose instalados.
- Porta 8080 livre (API) e 8081 livre (Keycloak), 1433 (SQL Server).

## Subir Ambiente Completo

```powershell
# Construir artefato (opcional - build também ocorre no compose se não existir imagem)
./mvnw package -DskipTests

# Subir serviços
docker compose up --build -d

# Ver logs da API
docker compose logs -f api
```

## Serviços
- Keycloak: http://localhost:8081
- API Quarkus: http://localhost:8080
- SQL Server: localhost:1433

## Keycloak
O realm é importado automaticamente de `keycloak/realm-export.json`.

Credenciais administrativas do keycloak:
- Usuário: `admin`
- Senha: `admin`


Credenciais para gerar o token admin:
- Usuário: `useradmin`
- Senha: `123`

Credenciais para gerar o token user:
- Usuário: `user`
- Senha: `123`

## Autenticação
Fluxo demonstrativo da autenticação:

1. Gerar o token chamando o keycloak:

```shell
curl --location 'http://localhost:8081/realms/caixaverso/protocol/openid-connect/token' \
--header 'Content-Type: application/x-www-form-urlencoded' \
--data-urlencode 'grant_type=password' \
--data-urlencode 'client_id=api-caixaverso' \
--data-urlencode 'client_secret=super-secret' \
--data-urlencode 'username=user' \
--data-urlencode 'password=123'
```

2. Realizar a chamada autenticada passando o token:

```shell
curl --location 'http://localhost:8080/produtos-recomendados/agressivo' \
--header 'Authorization: Bearer <token gerado>'
```

## Endpoints Principais
| Método | Rota | Papel |
|--------|------|-------|
| POST | /simular-investimento | user, admin |
| GET | /simulacoes | user, admin |
| GET | /simulacoes/por-produto-dia | user, admin |
| GET | /investimentos/{clienteId} | user, admin |
| GET | /perfil-risco/{clienteId} | user, admin |
| GET | /produtos-recomendados/{perfil} | user, admin |
| GET | /telemetria | admin |

## Variáveis de Ambiente
| Variável | Função |
|----------|--------|
| QUARKUS_DATASOURCE_DB_KIND | Define tipo do banco (mssql). |
| QUARKUS_DATASOURCE_USERNAME | Usuário banco. |
| QUARKUS_DATASOURCE_PASSWORD | Senha banco. |
| QUARKUS_DATASOURCE_JDBC_URL | URL JDBC interna apontando para service `sqlserver`. |
| QUARKUS_OIDC_AUTH_SERVER_URL | URL interna (rede docker) do provedor OIDC. |
| QUARKUS_OIDC_TOKEN_ISSUER | Issuer esperado externamente (localhost). |
| QUARKUS_OIDC_CLIENT_ID | Client registrado no Keycloak. |
| QUARKUS_OIDC_CREDENTIALS_SECRET | Segredo do client. |

## Parar Ambiente
```powershell
docker compose down -v
```

## Execução Local (fora de containers)
```powershell
# Subir somente dependências (ex.: banco e keycloak)
docker compose up -d keycloak sqlserver

# Rodar Quarkus em dev mode (live reload)
./mvnw quarkus:dev
```
