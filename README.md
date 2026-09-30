# ReportCity

Plataforma colaborativa para registro e acompanhamento de problemas urbanos, desenvolvida na disciplina DIM0547 — Desenvolvimento de Sistemas Web II da UFRN.

O objetivo é centralizar relatos hoje dispersos em redes sociais e grupos de mensagens. Cidadãos poderão registrar ocorrências, confirmar problemas identificados por outras pessoas e acompanhar sua evolução. O projeto está na **Sprint 0**: somente a fundação técnica e o planejamento estão implementados.

## Stack planejada

- Kotlin 2.3 e Ktor 3.5 na API principal;
- Go 1.25 no futuro serviço de prioridade;
- PostgreSQL para persistência nas próximas sprints;
- gRPC e Protocol Buffers para comunicação futura entre serviços;
- Gradle Wrapper e mise para build, testes e ambiente;
- Docker Compose e GitHub Actions.

## Arquitetura

```text
Cliente → API Kotlin/Ktor → PostgreSQL
                    |
                    └── gRPC → Serviço Go de prioridade
```

A API será proprietária do domínio e do banco. O serviço Go calculará prioridades sem acesso direto ao PostgreSQL. Esses componentes serão integrados apenas nas sprints correspondentes.

## Estrutura do monorepo

```text
api/                 API Kotlin/Ktor
services/priority/   serviço Go de prioridade
protos/              futuros contratos Protocol Buffers
docs/                proposta e decisões do projeto
.github/workflows/   integração contínua
```

## Requisitos e configuração

Instale [mise](https://mise.jdx.dev/getting-started.html) e Docker Desktop. Na raiz do repositório, instale as versões de Java e Go declaradas pelo projeto:

```shell
mise install
```

O Gradle não precisa ser instalado globalmente: o projeto usa o Gradle Wrapper.

## Build, testes e CI local

```shell
mise run build
mise run test
mise run lint
mise run ci
```

`mise run ci` reproduz a verificação executada pelo GitHub Actions.

## Executar a API

```shell
mise exec -- ./gradlew :api:run
```

A API responde em `http://localhost:8080/health`.

No Windows PowerShell, use `./gradlew.bat :api:run` caso execute o Wrapper diretamente.

## Executar o serviço Go

```shell
mise exec -- go run ./services/priority
```

O serviço responde em `http://localhost:8081/health`.

## Docker Compose

```shell
mise run up
```

Na Sprint 0 não há infraestrutura para iniciar. Por isso, a task valida o arquivo Compose, que será ampliado quando banco e integração forem exigidos.

## Status da Sprint 0

- [x] Estrutura do monorepo criada.
- [x] API Ktor e serviço Go mínimos, compiláveis e testados.
- [x] Tasks `build`, `test`, `lint`, `up` e `ci` definidas no mise.
- [x] Workflow de CI para push e pull request.
- [x] Proposta e decisão de arquitetura documentadas.
- [x] Integrantes, matrículas, coorte e integração declarados.
- [ ] GitHub Project com pelo menos cinco itens, todos priorizados e três estimados.
- [ ] Vídeo de apresentação de cinco minutos.
