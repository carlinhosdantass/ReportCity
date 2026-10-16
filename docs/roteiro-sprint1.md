# Roteiro do vídeo — Sprint 1

## 0:00–0:30 — Introdução

- Apresentar a equipe e o ReportCity.
- Explicar que a sprint entrega a API de categorias e ocorrências.

## 0:30–2:00 — CRUD

- Mostrar `Category` e `Issue` e o relacionamento 1:N.
- Usar a Swagger UI para criar uma categoria e uma ocorrência.
- Mostrar consulta por ID, paginação e filtro por `categoryId`.

## 2:00–3:00 — Banco

- Executar `mise run up`.
- Mostrar as duas migrações SQL e explicar que o Flyway cria o esquema.
- Reiniciar a API e mostrar que os dados continuam no PostgreSQL.

## 3:00–4:00 — Arquitetura

- Mostrar os pacotes `domain`, `application` e `adapters`.
- Mostrar que o domínio não importa Ktor nem Exposed.
- Mostrar os repositórios PostgreSQL e em memória.

## 4:00–5:00 — Validação e encerramento

- Enviar uma entrada inválida e mostrar o erro `application/problem+json`.
- Mostrar a especificação OpenAPI e o CI verde.
- Informar que testes adicionais, teste de arquitetura, Go e gRPC ficam para a Sprint 2.

Os dois integrantes devem participar da apresentação. Depois de publicar o vídeo, adicionar o link ao README.
