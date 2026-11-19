# API de Controle de Estoque

API REST para controle de estoque, desenvolvida em **Kotlin** com **Spring Boot**. Ela cadastra categorias, produtos e fornecedores, registra entradas e saídas de estoque e protege as alterações com login (**JWT** e papéis de acesso).

Trabalho da disciplina de **Backend Web Services**.

## Autores

- Arthur Albert Schmaiske Quoos
- Eric Tan Hui Zhen
- João Vitor Krupa Inglês

## Link do Video:

https://youtu.be/dCisyv8gj5w

## Sumário

- [Tecnologias](#tecnologias)
- [Como executar](#como-executar)
- [Usuários de teste](#usuários-de-teste)
- [Modelo de dados](#modelo-de-dados)
- [Endpoints](#endpoints)
- [Autenticação e autorização](#autenticação-e-autorização)
- [Consulta com filtro e ordenação](#consulta-com-filtro-e-ordenação)
- [Regras de negócio](#regras-de-negócio)
- [Tratamento de erros e logs](#tratamento-de-erros-e-logs)
- [Configuração e perfis](#configuração-e-perfis)
- [Estrutura do projeto](#estrutura-do-projeto)
- [Autores](#autores)

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Kotlin e Java 21 | Linguagem e versão do JDK |
| Spring Boot 4 | Base da aplicação (Web MVC, Validation) |
| Spring Data JPA e Hibernate | Persistência |
| H2 | Banco de dados (em memória ou em arquivo) |
| Spring Security e jjwt | Autenticação com token JWT |
| springdoc-openapi | Documentação interativa (Swagger UI) |
| Gradle (Kotlin DSL) | Build |

## Como executar

**Requisito:** JDK 21 instalado.

1. Clone o repositório e entre na pasta do projeto.
2. Execute a aplicação:

   ```bash
   ./gradlew bootRun
   ```

   No Windows (prompt de comando ou PowerShell):

   ```bash
   gradlew.bat bootRun
   ```

   Também é possível abrir o projeto no IntelliJ IDEA e executar a classe `EstoqueApplication`.

3. Acesse a documentação interativa (Swagger UI):

   ```
   http://localhost:8080/api/
   ```

   Se não abrir, tente `http://localhost:8080/api/swagger-ui/index.html`.

Por padrão a aplicação sobe no perfil `dev`, com banco H2 em memória e dados de exemplo (veja [Configuração e perfis](#configuração-e-perfis)). O console do H2 fica em `http://localhost:8080/api/h2-console`, com a URL `jdbc:h2:mem:db`, usuário `sa` e senha `sa`.

## Usuários de teste

Criados automaticamente ao iniciar a aplicação:

| E-mail | Senha | Papel | Permissões |
|---|---|---|---|
| `admin@estoque.com` | `admin` | `ADMIN` | Tudo, incluindo apagar registros |
| `operador@estoque.com` | `1234` | `USER` | Consultar, cadastrar e alterar, mas não apagar |

### Como testar no Swagger

1. Faça `POST /users/login` com o e-mail e a senha de um dos usuários acima.
2. Copie o valor do campo `token` da resposta.
3. Clique em **Authorize** (cadeado no topo da página), cole **só o token** (sem escrever `Bearer`) e confirme.
4. Para trocar de usuário, clique em **Authorize** de novo, use **Logout** e cole o token novo.

## Modelo de dados

| Entidade | Descrição | Relações |
|---|---|---|
| `Categoria` | Agrupa os produtos | Um para muitos com `Produto` |
| `Produto` | Item do estoque (nome, código, preço, quantidade) | Muitos para um com `Categoria`; muitos para muitos com `Fornecedor`; um para muitos com `Movimentacao` |
| `Fornecedor` | Empresa que fornece produtos (nome, CNPJ, contato) | Muitos para muitos com `Produto` |
| `Movimentacao` | Entrada ou saída de estoque de um produto | Muitos para um com `Produto` |
| `User` | Usuário do sistema (para o login) | Muitos para muitos com `Role` |
| `Role` | Papel de acesso (`ADMIN` ou `USER`) | Muitos para muitos com `User` |

As duas relações de muitos para muitos usam tabelas intermediárias: `ProdutoFornecedor` e `UserRole`.

## Endpoints

A coluna **Acesso** indica o que a rota exige: **Livre** (sem token), **Login** (qualquer usuário autenticado) ou **ADMIN** (somente administradores).

### Usuários

| Método | URL | Descrição | Acesso |
|---|---|---|---|
| POST | `/users/login` | Faz login e devolve o token | Livre |
| GET | `/users` | Lista os usuários | Login |
| GET | `/users/{id}` | Consulta um usuário | Login |

### Categorias (CRUD completo)

| Método | URL | Descrição | Acesso |
|---|---|---|---|
| GET | `/categorias` | Lista as categorias, ordenadas por nome | Livre |
| POST | `/categorias` | Cadastra uma categoria | Livre |
| GET | `/categorias/{id}` | Consulta uma categoria | Livre |
| PUT | `/categorias/{id}` | Atualiza nome e descrição | Login |
| DELETE | `/categorias/{id}` | Remove uma categoria | ADMIN |

### Produtos (CRUD completo)

| Método | URL | Descrição | Acesso |
|---|---|---|---|
| GET | `/produtos` | Lista com [filtros e ordenação](#consulta-com-filtro-e-ordenação) | Livre |
| POST | `/produtos` | Cadastra um produto | Livre |
| GET | `/produtos/{id}` | Consulta um produto | Livre |
| PATCH | `/produtos/{id}` | Atualiza nome e/ou preço | Login |
| DELETE | `/produtos/{id}` | Remove um produto | ADMIN |

### Fornecedores

| Método | URL | Descrição | Acesso |
|---|---|---|---|
| GET | `/fornecedores` | Lista os fornecedores | Livre |
| POST | `/fornecedores` | Cadastra um fornecedor | Livre |
| GET | `/fornecedores/{id}` | Consulta um fornecedor | Livre |

### Associações

| Método | URL | Descrição | Acesso |
|---|---|---|---|
| PUT | `/produtos/{id}/fornecedores/{idFornecedor}` | Associa um fornecedor ao produto | Login |
| DELETE | `/produtos/{id}/fornecedores/{idFornecedor}` | Remove a associação (não apaga o produto nem o fornecedor) | ADMIN |
| POST | `/produtos/{id}/movimentacoes` | Registra uma entrada ou saída e atualiza o estoque | Livre |
| GET | `/produtos/{id}/movimentacoes` | Lista o histórico do produto, do mais recente ao mais antigo | Livre |

**Como funcionam as associações**

- **Produto e fornecedor (muitos para muitos):** os dois registros já existem, e o `PUT` apenas cria a ligação entre eles, gravada na tabela `ProdutoFornecedor`. Repetir a chamada não altera nada (responde 204). O `DELETE` remove só a ligação.
- **Produto e movimentação (um para muitos):** a movimentação é criada já ligada ao produto da URL. Uma entrada soma a quantidade ao estoque, e uma saída subtrai. O registro da movimentação e a atualização do estoque acontecem na mesma transação.

### Exemplos de corpo das requisições

`POST /categorias`

```json
{ "nome": "Bebidas", "descricao": "Sucos, refrigerantes e águas" }
```

`POST /produtos`

```json
{
  "nome": "Suco de laranja 1L",
  "codigo": "BEB-001",
  "preco": 8.50,
  "quantidade": 20,
  "idCategoria": 1
}
```

`POST /produtos/{id}/movimentacoes`

```json
{ "tipo": "ENTRADA", "quantidade": 10 }
```

`POST /users/login`

```json
{ "email": "admin@estoque.com", "password": "admin" }
```

## Autenticação e autorização

- No login, a API gera um **token JWT** com o e-mail do usuário, os papéis, o emissor e a validade. O token vale **1 hora** para usuários comuns e **4 horas** para administradores (configurável).
- O cliente envia o token no header `Authorization: Bearer <token>`.
- Um filtro lê o token a cada requisição e marca o usuário como autenticado, com os papéis (`ROLE_ADMIN` ou `ROLE_USER`).
- As regras de acesso ficam em `SecurityConfig`:
  - Consultas e cadastros são livres.
  - `PUT` e `PATCH` exigem estar logado.
  - `DELETE` exige o papel `ADMIN`.
  - `/users/**` exige estar logado, exceto o login.

| Situação | Resposta |
|---|---|
| Sem token em uma rota protegida | **401** |
| Logado como `USER` tentando apagar | **403** |
| Logado como `ADMIN` apagando | **200** |

## Consulta com filtro e ordenação

`GET /produtos` aceita os parâmetros de query abaixo, todos opcionais e combináveis:

| Parâmetro | Descrição | Padrão |
|---|---|---|
| `categoria` | Nome exato da categoria | sem filtro |
| `nome` | Trecho do nome do produto, sem diferenciar maiúsculas e minúsculas | sem filtro |
| `quantidadeMaxima` | Produtos com estoque menor ou igual a este valor | sem filtro |
| `ordenarPor` | Campo da ordenação: `nome`, `preco` ou `quantidade` | `nome` |
| `sortDir` | Direção: `ASC` ou `DESC` | `ASC` |

Exemplo, bebidas com até 10 unidades em estoque, do mais caro para o mais barato:

```
GET /api/produtos?categoria=Bebidas&quantidadeMaxima=10&ordenarPor=preco&sortDir=DESC
```

Um valor inválido em `ordenarPor` ou `sortDir` retorna **400**.

## Regras de negócio

- O código do produto, o nome da categoria e o CNPJ do fornecedor não podem se repetir (**409**).
- Uma categoria que ainda tem produtos não pode ser removida (**409**).
- Um produto que já tem movimentações não pode ser removido (**409**).
- A quantidade do produto só muda por movimentações, nunca pelo `PATCH`.
- Uma saída maior que o estoque disponível é recusada com **409** (`EstoqueInsuficienteException`), e nada é gravado.

## Tratamento de erros e logs

**Erros.** O `GlobalExceptionHandler` (`@RestControllerAdvice`) converte as exceções em respostas JSON padronizadas (`ProblemDetail`):

| Status | Quando |
|---|---|
| 400 | JSON malformado, tipo inválido na URL, direção ou campo de ordenação inválido |
| 401 | Sem token, token inválido ou login incorreto |
| 403 | Usuário sem permissão para a ação |
| 404 | Recurso não encontrado |
| 409 | Duplicidade, remoção bloqueada ou estoque insuficiente |
| 422 | Campos inválidos no corpo da requisição |
| 500 | Erro inesperado (o detalhe fica só no log) |

**Logs.** Os services registram eventos com SLF4J: `info` ao criar e atualizar, `warn` ao remover registros ou recusar uma saída. Os logs aparecem no console e também no arquivo `logs/spring.log`.

## Configuração e perfis

As configurações ficam fora do código, em arquivos YAML:

- **`application.yaml`:** configuração geral e os perfis `dev` e `prod`.
- **`security.yaml`:** emissor, chave secreta, tempo de expiração do token e origens permitidas pelo CORS. É lido pela classe `SecurityProperties`.

### Perfis

| | `dev` (padrão) | `prod` |
|---|---|---|
| Banco | H2 em memória | H2 em arquivo (`data/estoque.mv.db`) |
| SQL no console | Sim | Não |
| Console do H2 | Ligado | Desligado |
| Dados de exemplo | Criados (`DevDataLoader`) | Não criados |

Para executar com o perfil `prod`:

```bash
SPRING_PROFILES_ACTIVE=prod ./gradlew bootRun
```

No Windows, defina a variável de ambiente antes de executar (por exemplo, `set SPRING_PROFILES_ACTIVE=prod`), ou use a configuração de execução do IntelliJ.

### Sobrescrevendo configurações por variável de ambiente

Variáveis de ambiente têm prioridade sobre os arquivos, sem alterar o código:

| Variável | Propriedade |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Perfil ativo |
| `SECURITY_SECRET` | Chave de assinatura do token (mínimo de 32 caracteres) |
| `SECURITY_ISSUER` | Emissor do token |
| `SECURITY_EXPIRE_HOURS` | Validade do token para usuários comuns, em horas |
| `SECURITY_ADMIN_EXPIRE_HOURS` | Validade do token para administradores, em horas |

> A chave que vem no `security.yaml` serve apenas para desenvolvimento. Em um ambiente real, defina `SECURITY_SECRET` por variável de ambiente.

## Estrutura do projeto

O código é organizado por funcionalidade. Cada pacote tem a entidade, o repository, o service, o controller e as classes de request e response.

```
src/main/kotlin/br/pucpr/estoque
├── categorias/        Categoria, repository, service, controller, requests e responses
├── produtos/          Produto, repository (com filtro), service, controller, requests, responses e SortDir
├── fornecedores/      Fornecedor, repository, service, controller, requests e responses
├── movimentacoes/     Movimentacao, TipoMovimentacao, EstoqueInsuficienteException, repository, service e controller
├── users/             User, login (service e controller) e responses
├── roles/             Role e RoleRepository
├── security/          JWT, JwtTokenFilter, SecurityConfig, SecurityProperties e OpenApiConfig
├── BootStrapper.kt    Cria os papéis e os usuários ao iniciar
├── DevDataLoader.kt   Cria os dados de exemplo (somente no perfil dev)
├── GlobalExceptionHandler.kt
└── EstoqueApplication.kt

src/main/resources
├── application.yaml
└── security.yaml
```

O caminho de uma requisição é sempre **Controller → Service → Repository → Banco**, e a resposta volta convertida em um Response.
