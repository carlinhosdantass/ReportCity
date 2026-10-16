# ReportCity

API para cadastrar e consultar problemas urbanos, desenvolvida na disciplina DIM0547 — Desenvolvimento de Sistemas Web II da UFRN.

O sistema possui duas entidades: **Category** e **Issue**. Uma categoria pode ter várias ocorrências, e cada ocorrência pertence a uma categoria.

## Tecnologias

- Kotlin, Ktor e Gradle;
- PostgreSQL, Exposed, HikariCP e Flyway;
- OpenAPI e Swagger UI;
- Go no serviço de prioridade;
- mise, Docker Compose e GitHub Actions.

## Estrutura

```text
api/src/main/kotlin/br/ufrn/reportcity/
├── domain/       entidades e interfaces dos repositórios
├── application/  regras e casos de uso
└── adapters/     HTTP, PostgreSQL e memória
```

O domínio não importa Ktor, Exposed, JDBC ou outras bibliotecas de infraestrutura.

## Preparar o ambiente

Instale [mise](https://mise.jdx.dev/getting-started.html) e Docker Desktop. Depois execute:

```shell
mise install
mise run up
```

`mise run up` inicia o PostgreSQL com estes valores padrão:

```text
Banco: reportcity
Usuário: reportcity
Senha: reportcity
Porta: 5432
```

Eles podem ser alterados pelas variáveis `POSTGRES_DB`, `POSTGRES_USER`, `POSTGRES_PASSWORD` e `POSTGRES_PORT`.

## Executar a API

Com o PostgreSQL iniciado:

```shell
mise run run
```

A API usa estas variáveis, todas opcionais no ambiente local:

```text
DB_URL=jdbc:postgresql://localhost:5432/reportcity
DB_USER=reportcity
DB_PASSWORD=reportcity
PORT=8080
```

O Flyway aplica automaticamente as migrações ao iniciar a API.

## Rotas

```text
GET    /health
POST   /categories
GET    /categories
GET    /categories/{id}
PUT    /categories/{id}
DELETE /categories/{id}
POST   /issues
GET    /issues?page=0&size=10&categoryId=1
GET    /issues/{id}
PUT    /issues/{id}
DELETE /issues/{id}
```

O tamanho máximo de página é 100. Uma categoria com ocorrências não pode ser excluída.

## OpenAPI

- Swagger UI: `http://localhost:8080/docs`
- Especificação gerada: `http://localhost:8080/docs/documentation.yaml`

A especificação é gerada a partir das descrições das rotas Ktor.

## Build e testes

```shell
mise run build
mise run test
mise run lint
mise run ci
```

`mise run ci` executa as mesmas verificações do GitHub Actions. Os testes adicionais e o teste de arquitetura ficaram para a Sprint 2, conforme orientação da disciplina.

Para parar o banco:

```shell
mise run down
```

## Serviço Go

```shell
mise exec -- go run ./services/priority
```

O serviço responde em `http://localhost:8081/health`. O cálculo de prioridade e o gRPC serão implementados somente na Sprint 2.

## Sprint 1

- [x] Duas entidades relacionadas com CRUD completo.
- [x] Filtro por categoria e paginação no SQL.
- [x] Camadas separadas e domínio sem framework.
- [x] PostgreSQL no Docker Compose.
- [x] Migrações versionadas com Flyway.
- [x] Validação e erros em `application/problem+json`.
- [x] OpenAPI gerado do código e Swagger UI.
- [ ] GitHub Projects atualizado e cartões ligados aos pull requests.
- [ ] Link do vídeo de cinco minutos.

## Equipe

- Carlos Eduardo Fonseca Dantas — 20240026911.
- Luis Carlos Tavares Xavier — 20240029664.
- Coorte A.
- Sem integração com outra disciplina.
