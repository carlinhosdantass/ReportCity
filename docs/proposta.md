# Proposta — ReportCity

## Identificação

- **Integrantes e matrículas:**
  - Carlos Eduardo Fonseca Dantas — 20240026911.
  - Luis Carlos Tavares Xavier — 20240029664.
- **Papéis:** a definir pela equipe.
- **Coorte de apresentação:** Coorte A.
- **Integração com outra disciplina:** não há.
- **GitHub Project:** link a adicionar após a criação do quadro.

## Visão do produto

Para moradores que precisam registrar problemas urbanos, o **ReportCity** é uma API de cadastro e consulta de ocorrências. Ela reúne problemas como buracos, iluminação defeituosa e lixo acumulado. Diferente de mensagens espalhadas em vários lugares, o ReportCity organiza os registros por categoria e bairro.

O sistema não terá integração oficial com prefeituras.

## Problema e público-alvo

Relatos de problemas urbanos ficam espalhados e são difíceis de consultar. O público-alvo são moradores que desejam registrar e encontrar essas ocorrências em um único lugar.

**Hipótese de valor:** acreditamos que os moradores usarão o sistema porque as ocorrências ficarão organizadas e fáceis de encontrar.

## MVP

O MVP contém somente:

- cadastro, listagem, edição e exclusão de categorias;
- cadastro, listagem, consulta por ID, edição e exclusão de ocorrências;
- filtro de ocorrências por categoria;
- paginação da listagem de ocorrências.

### Fora do MVP

- usuários e funcionalidades sociais;
- confirmações, votações e comentários;
- fotografias, mapas e geolocalização;
- notificações e histórico;
- status, rankings e dashboard;
- integração com prefeituras ou APIs externas.

Autenticação, autorização, cache, segurança e deploy serão adicionados somente quando a disciplina exigir. Eles não fazem parte das funções principais do produto.

## Entidades

### Categoria

- ID;
- nome.

### Ocorrência

- ID;
- título;
- descrição;
- bairro;
- categoria.

Uma ocorrência pertence a uma categoria. Não há outras entidades ou relacionamentos planejados para o domínio.

## Escolha tecnológica

Escolhemos **Kotlin com Ktor** em vez de Java com Quarkus. Kotlin tem uma sintaxe simples e funciona com bibliotecas Java. Ktor é leve e suficiente para uma API pequena. Nas próximas sprints, também poderemos usar Flyway e Testcontainers.

Quarkus também seria uma boa opção, principalmente para containers. Preferimos Ktor para manter a aplicação pequena e aprender uma tecnologia diferente.

## Arquitetura e responsabilidades

```text
Cliente → API Kotlin/Ktor → PostgreSQL
                    |
                    └── gRPC → Serviço Go
```

A API Ktor cuidará de Categoria, Ocorrência, validação, banco, filtro e paginação. Somente ela acessará o PostgreSQL.

O serviço Go receberá um número da API e devolverá uma pontuação simples. Esse cálculo é separado e não precisa acessar o banco. A comunicação com a API será definida na Sprint 2 usando gRPC.

Na Sprint 0 existem apenas os endpoints `/health`. PostgreSQL, CRUD, gRPC e o cálculo não estão implementados.

## Backlog inicial

| Prioridade | História | Critérios de aceitação | Estimativa | Sprint |
|---|---|---|---:|---:|
| P1 | Como responsável pelo sistema, quero gerenciar categorias para organizar as ocorrências | Cadastrar, listar, editar e excluir; nome obrigatório | 3 | 1 |
| P1 | Como morador, quero gerenciar ocorrências para registrar e consultar problemas urbanos | CRUD completo; relacionamento com categoria; consulta por ID; paginação e filtro por categoria | 5 | 1 |
| P1 | Como sistema, quero calcular uma pontuação simples em Go para atender ao processamento separado | Uma operação determinística; comunicação gRPC; teste de integração | 3 | 2 |
| P2 | Como morador, quero que consultas frequentes sejam rápidas | Cache mínimo com política, invalidação e métricas documentadas | 3 | 3 |
| P1 | Como responsável pelo sistema, quero proteger a API para restringir operações indevidas | Autenticação e autorização mínimas exigidas pela entrega final; teste de acesso indevido | 5 | Final |

Todos os itens estão priorizados e estimados. Eles devem ser cadastrados em um GitHub Project vinculado ao repositório.

## Requisitos técnicos das próximas entregas

Cada sprint terá somente os requisitos técnicos obrigatórios: arquitetura e migrações na Sprint 1, Protobuf e gRPC na Sprint 2, deploy e observabilidade na Sprint 3, e segurança e documentação na entrega final.
