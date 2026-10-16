# ReportCity

API para cadastrar e consultar problemas urbanos, desenvolvida na disciplina DIM0547 — Desenvolvimento de Sistemas Web II da UFRN.

O sistema terá apenas duas entidades: **Categoria** e **Ocorrência**. Uma ocorrência pertence a uma categoria. Na Sprint 0, existem somente a estrutura do projeto, os testes e as rotas `/health`.

## Stack

- Kotlin 2.3 e Ktor 3.5;
- Go 1.25;
- PostgreSQL nas próximas sprints;
- gRPC e Protocol Buffers a partir da Sprint 2;
- Gradle Wrapper, mise, Docker Compose e GitHub Actions.

## Arquitetura planejada

```text
Cliente → API Kotlin/Ktor → PostgreSQL
                    |
                    └── gRPC → Serviço Go
```

A API cuidará das categorias, das ocorrências e do banco. O serviço Go fará um cálculo simples e não acessará o banco. Veja os detalhes em [docs/proposta.md](docs/proposta.md).

## Estrutura

```text
api/                 API Kotlin/Ktor
services/priority/   serviço Go
protos/              futuros contratos gRPC
docs/                proposta do projeto
.github/workflows/   integração contínua
```

## Configuração

Instale [mise](https://mise.jdx.dev/getting-started.html) e Docker Desktop. Depois execute:

```shell
mise install
```

O Gradle é executado pelo Wrapper e não precisa ser instalado separadamente.

## Build e testes

```shell
mise run build
mise run test
mise run lint
mise run ci
```

`mise run ci` executa as mesmas verificações do GitHub Actions.

## Executar a API

```shell
mise exec -- ./gradlew :api:run
```

No Windows, o Wrapper também pode ser chamado com `./gradlew.bat :api:run`. A API responde em `http://localhost:8080/health`.

## Executar o serviço Go

```shell
mise exec -- go run ./services/priority
```

O serviço responde em `http://localhost:8081/health`.

## Docker Compose

```shell
mise run up
```

Na Sprint 0, essa task apenas valida o arquivo Compose.

## Sprint 0

- [x] Monorepo com `api/`, `services/`, `protos/` e `docs/`.
- [x] API Ktor e serviço Go mínimos, com testes.
- [x] Tasks de build, teste, lint, Compose e CI.
- [x] GitHub Actions para push e pull request.
- [x] Proposta, arquitetura, equipe, coorte e integração documentadas.
- [x] Papéis dos integrantes definidos.
- [x] GitHub Project com pelo menos cinco itens priorizados e três estimados.
- [x] CI verde na branch `main` após o push.
- [x] Vídeo de apresentação de cinco minutos.
