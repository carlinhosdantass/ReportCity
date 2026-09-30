# Proposta — ReportCity

## Identificação

- **Integrantes e matrículas:**
  - Carlos Eduardo Fonseca Dantas — 20240026911.
  - Luis Carlos Tavares Xavier — 20240029664.
- **Papéis:** a preencher pela equipe.
- **Coorte de apresentação (A/B):** Coorte A.
- **Integração com outra disciplina:** não há integração com outra disciplina.
- **Backlog no GitHub Projects:** link a adicionar após a criação do quadro.

## Visão do produto

Para moradores interessados em registrar e acompanhar problemas urbanos do município,
que hoje encontram relatos dispersos em redes sociais e grupos de mensagens,
o **ReportCity** é uma plataforma colaborativa de registro e acompanhamento de ocorrências urbanas
que centraliza relatos, permite confirmações por outros cidadãos e torna visível a evolução do atendimento.
Diferente de publicações isoladas em canais informais,
nosso produto organiza os problemas por categoria, estado e prioridade em um histórico consultável.

O ReportCity não será inicialmente integrado oficialmente à prefeitura.

**Hipótese de valor:** acreditamos que moradores registrarão e confirmarão ocorrências na plataforma porque a centralização reduz a dispersão das informações e facilita acompanhar problemas compartilhados pela comunidade.

## Público-alvo

Moradores interessados em registrar e acompanhar problemas urbanos do município.

## Definição do MVP

### Dentro do MVP ao longo do semestre

- usuários e autenticação/autorização;
- categorias de ocorrências;
- cadastro, consulta, filtros e paginação de ocorrências;
- confirmação de uma ocorrência por outros cidadãos;
- acompanhamento pelos estados `ABERTA`, `EM_ANALISE` e `RESOLVIDA`;
- cálculo de prioridade (`LOW`, `MEDIUM` ou `HIGH`);
- cache, conforme a evolução exigida pela disciplina.

### Fora do MVP

- integração oficial com a prefeitura;
- aplicativo mobile nativo;
- inteligência artificial e reconhecimento de imagens;
- mapas interativos e geolocalização em tempo real;
- WhatsApp, SMS e redes sociais;
- APIs externas não essenciais.

## Entidades principais

- **User:** cidadão autenticado que registra e acompanha ocorrências.
- **Category:** classificação do tipo de problema urbano.
- **Issue:** relato do problema, sua categoria, estado e dados de acompanhamento.
- **Confirmation:** registro de que outro cidadão identificou a mesma ocorrência.

Essas entidades descrevem o domínio planejado; sua implementação pertence às próximas sprints.

## Decisão tecnológica: Kotlin/Ktor

Foi escolhido **Kotlin com Ktor** em vez de Java com Quarkus. Kotlin oferece sintaxe concisa, segurança contra valores nulos e interoperabilidade com o ecossistema Java. Ktor permite iniciar com uma API pequena e assíncrona, sem impor uma estrutura maior do que o necessário, e suas corrotinas serão adequadas à futura chamada gRPC. A escolha também traz diferenciação didática ao permitir explorar uma stack moderna da JVM.

Quarkus seria uma alternativa sólida por sua inicialização rápida, integração ampla e foco em containers. Para este projeto, porém, a flexibilidade e o modelo leve do Ktor combinam melhor com a evolução incremental prevista, mantendo acesso futuro a bibliotecas como Flyway e Testcontainers.

## Arquitetura e divisão de responsabilidades

```text
Cliente
   |
   v
API Kotlin/Ktor
   |
   v
PostgreSQL
```

```text
API Kotlin/Ktor
   |
   | gRPC
   v
Serviço Go de prioridade
```

A API Ktor será o serviço principal e concentrará usuários, categorias, ocorrências, confirmações, persistência, filtros, paginação, autenticação/autorização e regras centrais do domínio. Ela será a única proprietária do banco PostgreSQL.

O serviço Go terá responsabilidade única: calcular a prioridade das ocorrências futuramente a partir de dados fornecidos pela API, como confirmações, tempo em aberto e categoria. Esse processamento isolado permite explorar um serviço pequeno, concorrente e de inicialização rápida. O serviço não acessará diretamente o banco da API; a comunicação futura ocorrerá exclusivamente por um contrato gRPC versionado em `protos/`.

Na Sprint 0, PostgreSQL, gRPC e o cálculo de prioridade são apenas decisões arquiteturais e não estão implementados.

## Backlog inicial

| Prioridade | História | Critérios de aceitação | Estimativa | Sprint |
|---|---|---|---:|---:|
| P1 | Como cidadão, quero cadastrar uma ocorrência para registrar um problema urbano | Entrada validada; ocorrência criada como `ABERTA`; resposta HTTP coerente | 5 | 1 |
| P1 | Como cidadão, quero consultar ocorrências para acompanhar os problemas registrados | Lista paginada; filtro por categoria e estado; consulta por identificador | 5 | 1 |
| P1 | Como administrador, quero gerenciar categorias para classificar as ocorrências | Criar, listar, alterar e remover; nome obrigatório e único | 3 | 1 |
| P1 | Como cidadão, quero confirmar uma ocorrência para indicar que também identifiquei o problema | Uma confirmação por usuário e ocorrência; total atualizado | 3 | 1 |
| P1 | Como sistema, quero calcular a prioridade para ordenar os problemas por relevância | Serviço Go recebe os fatores via gRPC; retorna `LOW`, `MEDIUM` ou `HIGH`; falhas são tratadas | 5 | 2 |
| P2 | Como cidadão, quero autenticar-me para usar recursos vinculados à minha identidade | Credenciais válidas geram sessão/token; rotas protegidas recusam acesso anônimo | 5 | Final |
| P1 | Como responsável, quero alterar o estado de uma ocorrência para refletir seu andamento | Somente transições válidas `ABERTA → EM_ANALISE → RESOLVIDA`; alteração persistida | 3 | 1 |
| P2 | Como cidadão, quero consultas frequentes rápidas para acompanhar ocorrências sem demora | Política de cache documentada; invalidação definida; métricas de acerto e erro expostas | 5 | 3 |

Todos os itens estão priorizados e mais de três estão estimados. Antes da entrega, eles devem ser cadastrados em um GitHub Project vinculado ao repositório, preservando histórias e critérios de aceitação.
