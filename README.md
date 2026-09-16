# 🎓 CampusGigs API

API REST desenvolvida para o **Projeto Diamante**, plataforma de freelas entre
alunos de uma universidade: um aluno se cadastra, publica um serviço (freela) e
outro aluno, autenticado, contrata esse serviço.

O backend foi desenvolvido com **Java, Spring Boot, PostgreSQL, Flyway e Spring
Security com autenticação JWT utilizando chaves RSA**.

---

# 👥 Integrantes

- João Vitor Biribilli - RM565594
- Pietro Paranhos Wilhelm - RM561378
- Gabriel Neris Losano - RM564093

---

# 📌 Sobre o projeto

A API cobre o ciclo completo descrito no enunciado:

- cadastro de aluno, com endereço resolvido automaticamente a partir do CEP;
- autenticação e emissão de token de acesso (JWT);
- publicação, listagem, edição e encerramento de serviços (freelas);
- contratação de serviços por outros alunos;
- autorização por papel (`ADMIN` / `USER`), com regra de dono do recurso;
- versionamento do banco de dados com Flyway;
- ambiente reprodutível via Docker;
- integração com serviço externo (ViaCEP) usando cliente HTTP declarativo
  (`HttpExchange`).

---

# 🧰 Tecnologias utilizadas

- Java 17
- Spring Boot 3
- Spring MVC
- Spring Data JPA
- Spring Security
- OAuth2 Resource Server
- JWT assinado com RSA
- BCrypt
- PostgreSQL
- Flyway
- Springdoc OpenAPI / Swagger
- Lombok
- Maven
- Docker / Docker Compose

---

# 🏗️ Arquitetura

Arquitetura em camadas:

```
Controller → Service → Repository → PostgreSQL
```

Pacotes principais:

```
src/main/java/br/com/fiap/campusgigs/

├── client/       Cliente HTTP declarativo (HttpExchange) para o ViaCEP
├── config/       Configurações da aplicação e segurança (RSA, SecurityConfig)
├── controller/   Endpoints REST
├── dto/
│   ├── request/  Dados recebidos pela API
│   └── response/ Dados devolvidos pela API
├── enums/        Enumerações do domínio
├── model/        Entidades JPA
├── repository/   Acesso ao banco com Spring Data JPA
├── security/     Representação do usuário autenticado
├── service/      Regras de negócio
└── validation/   Tratamento global de validações e erros
```

Recursos:

```
src/main/resources/
├── application.yml
├── certs/            (chaves RSA, geradas localmente - não versionadas)
└── db/migration/     (migrations Flyway)
```

---

# 🔐 Segurança

Autenticação via **Spring Security + JWT**, com o token assinado por um par de
chaves **RSA**:

```
private_key.pem
public_key.pem
```

A senha dos usuários é armazenada com **BCrypt**.

A aplicação possui dois papéis:

```
ROLE_ADMIN
ROLE_USER
```

O papel do usuário é incluído como claim `role` no JWT.

## Regra de autorização

Diferente de uma checagem simples de papel, a regra "só o dono edita/encerra o
próprio serviço, exceto ADMIN" depende do **dado** (quem é o prestador do
registro), não só do papel de quem está logado — por isso essa verificação
fica na camada de `service` (`ServicoService.garantirDonoOuAdmin`), e não em
`@PreAuthorize` estático no controller.

Regras aplicadas:

- qualquer usuário autenticado pode publicar e contratar serviços;
- só o dono edita/encerra o próprio serviço — ADMIN pode encerrar qualquer um;
- um usuário não pode contratar o próprio serviço;
- operações que alteram dados exigem token válido.

---

# 🔓 Endpoints públicos

```
POST /usuarios
POST /login
GET  /servicos
GET  /servicos/**

/swagger-ui/**
/v3/api-docs/**
```

Os demais endpoints exigem `Authorization: Bearer <token>`.

---

# 🔑 Autenticação

```
POST /login
Content-Type: application/json
```

```json
{
  "email": "maria@fiap.com.br",
  "senha": "123456"
}
```

Resposta:

```json
{ "token": "eyJhbGciOiJSUzI1NiJ9..." }
```

---

# 👤 Usuário de demonstração (seed)

Criado pela migration `V2__seed_admin.sql`, necessário para testar a regra
"ADMIN encerra qualquer serviço":

```
E-mail: admin@campusgigs.com
Senha:  admin123
```

---

# 📚 Endpoints

| Método | Rota                                | Autenticação            |
|--------|--------------------------------------|--------------------------|
| POST   | `/usuarios`                          | pública                  |
| POST   | `/login`                             | pública (JSON)           |
| POST   | `/servicos`                          | autenticado              |
| GET    | `/servicos`                          | pública                  |
| PUT    | `/servicos/{id}`                     | dono ou ADMIN            |
| POST   | `/servicos/{id}/encerrar`            | dono ou ADMIN            |
| POST   | `/servicos/{servicoId}/contratacoes` | autenticado (não-dono)   |

Documentação interativa: `/swagger-ui.html`.

---

# 🌍 Integração externa — ViaCEP

Ao cadastrar um usuário, a API consulta a ViaCEP através de um **cliente HTTP
declarativo** (`org.springframework.web.service.annotation.GetExchange`):

```
GET https://viacep.com.br/ws/{cep}/json
```

`ViaCepClient` é uma interface anotada com `@GetExchange`; o Spring gera a
implementação em tempo de execução via `HttpServiceProxyFactory`
(`RestClientConfig`). Se o CEP não existir, ou a ViaCEP falhar/demorar, a
operação é interrompida com `400 Bad Request` em vez de salvar o cadastro
incompleto.

---

# ✅ Validações

A API utiliza Jakarta Bean Validation. Entre as validações implementadas:

- nome, e-mail, senha e CEP obrigatórios no cadastro;
- e-mail em formato válido;
- senha com no mínimo 6 caracteres;
- CEP com 8 dígitos numéricos;
- título, descrição, categoria e preço obrigatórios no serviço;
- preço maior que zero.

---

# 🌐 Códigos HTTP utilizados

```
200 OK        — operação realizada com sucesso
201 Created   — registro criado com sucesso
400 Bad Request — dados inválidos ou regra de negócio não atendida
401 Unauthorized — não autenticado, credenciais inválidas ou token expirado
403 Forbidden — autenticado, mas sem permissão para o recurso
404 Not Found — registro não encontrado
```

Toda violação (validação, autorização ou negócio) passa pelo
`GlobalExceptionHandler` (pacote `validation`), retornando um JSON padronizado,
sem stack trace.

---

# 🗄️ Flyway

Migrations em `src/main/resources/db/migration`:

```
V1__create_schema.sql   schema inicial (usuario, servico, contratacao)
V2__seed_admin.sql      usuário ADMIN de demonstração
```

Executadas automaticamente na inicialização (`spring.flyway.enabled=true`).

---

# 🐳 Docker

```
docker compose up --build
```

Sobe API + PostgreSQL juntos, sem exigir instalação manual de banco.

---

# ▶️ Como executar

## 1. Gerar as chaves RSA (obrigatório, antes do build)

```bash
mkdir -p src/main/resources/certs
cd src/main/resources/certs
openssl genrsa -out private_key.pem 2048
openssl rsa -in private_key.pem -pubout -out public_key.pem
cd ../../../../..
```

As chaves não são versionadas no Git (estão no `.gitignore`).

## 2. Subir com Docker (recomendado)

```bash
docker compose up --build
```

Sobe API + PostgreSQL juntos, sem exigir instalação manual de banco. As
migrations do Flyway rodam automaticamente. A API fica disponível em
`http://localhost:8080`.

## 3. Rodar localmente sem Docker (alternativa)

Requer Java 17+, Maven e um PostgreSQL acessível:

```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=campusgigs
export DB_USER=campusgigs
export DB_PASSWORD=campusgigs

mvn spring-boot:run
```

---

# 📮 Exemplo de chamada autenticada

**1. Cadastro** (público):

```bash
curl -X POST http://localhost:8080/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Souza",
    "email": "maria@fiap.com.br",
    "senha": "123456",
    "cep": "01310930"
  }'
```

**2. Login** (retorna o JWT):

```bash
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{ "email": "maria@fiap.com.br", "senha": "123456" }'
```

Resposta:

```json
{ "token": "eyJhbGciOiJSUzI1NiJ9..." }
```

**3. Chamada autenticada** (usa o token do passo anterior):

```bash
curl -X POST http://localhost:8080/servicos \
  -H "Authorization: Bearer eyJhbGciOiJSUzI1NiJ9..." \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Aulas de Cálculo I",
    "descricao": "Reforço para provas e listas",
    "categoria": "Tutoria",
    "preco": 50.00
  }'
```

---

# 📋 Checkpoints do Projeto Diamante

Cada checkpoint corresponde a um commit com justificativa curta (1–3 linhas)
de uma decisão tomada naquele trecho.

| CP  | Entrega                                                    |
|-----|-------------------------------------------------------------|
| CP1 | Ambiente sobe via Docker; primeira migration com schema inicial |
| CP2 | Cadastro e autenticação funcionando (senha protegida)       |
| CP3 | Emissão e validação de token nos endpoints protegidos       |
| CP4 | Regras de autorização por papel aplicadas                   |
| CP5 | Integração com serviço externo (ViaCEP) e revisão final     |

## Arquivos por checkpoint

**CP1** — schema inicial + Docker:
```
pom.xml, Dockerfile, docker-compose.yml, .dockerignore, .gitignore
src/main/resources/application.yml
src/main/resources/db/migration/V1__create_schema.sql
src/main/java/.../CampusgigsApplication.java
src/main/java/.../enums/*.java
src/main/java/.../model/*.java
src/main/java/.../repository/*.java
```

**CP2** — cadastro e autenticação com senha protegida (SEM ViaCEP ainda):
```
src/main/resources/db/migration/V2__seed_admin.sql
src/main/java/.../dto/request/UsuarioRequest.java
src/main/java/.../dto/response/UsuarioResponse.java
src/main/java/.../security/CustomUserDetailsService.java
src/main/java/.../service/UsuarioService.java   (versão simplificada, sem CEP - veja o chat)
src/main/java/.../controller/UsuarioController.java
src/main/java/.../validation/*.java
```

**CP3** — emissão/validação de JWT:
```
src/main/resources/certs/  (gerado localmente, não commitado)
src/main/java/.../config/RsaKeyProperties.java
src/main/java/.../config/SecurityConfig.java
src/main/java/.../security/AutenticadoService.java
src/main/java/.../service/TokenService.java
src/main/java/.../dto/request/LoginRequest.java
src/main/java/.../controller/AuthController.java
```

**CP4** — autorização por papel:
```
src/main/java/.../dto/request/ServicoRequest.java
src/main/java/.../dto/response/ServicoResponse.java
src/main/java/.../dto/response/ContratacaoResponse.java
src/main/java/.../service/ServicoService.java
src/main/java/.../service/ContratacaoService.java
src/main/java/.../controller/ServicoController.java
src/main/java/.../controller/ContratacaoController.java
```

**CP5** — integração ViaCEP + revisão final:
```
src/main/java/.../client/ViaCepClient.java
src/main/java/.../dto/response/ViaCepResponse.java
src/main/java/.../config/RestClientConfig.java
src/main/java/.../service/UsuarioService.java   (agora com a chamada ao ViaCEP - diff sobre o CP2)
README.md
```

---

# 🎯 Requisitos atendidos

```
✅ JWT assinado com RSA (OAuth2 Resource Server)
✅ Autorização por papel (ADMIN / USER) via claim no token
✅ Regra de dono do recurso, aplicada na camada de service
✅ Senhas com BCrypt
✅ Flyway para versionamento do banco
✅ Docker Compose (API + PostgreSQL) sem instalação manual de banco
✅ Cliente HTTP declarativo (HttpExchange) para o ViaCEP
✅ Tratamento de CEP inexistente / serviço externo indisponível
✅ Tratamento centralizado de erros (pacote validation), sem stack trace
✅ Validação de dados de entrada (Jakarta Bean Validation)
```
