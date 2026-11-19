# ZelaBairro API — Domain Driven Design Using Java

Global Solution 2026/2 · tema **CitizenTech** · FIAP · turma **1TDSPV**

API REST do **ZelaBairro**: o cidadão registra problemas urbanos do bairro (buraco, lixo, poda...), apoia ocorrências de outros cidadãos e acompanha o andamento; gestores de órgãos públicos atualizam o status num painel restrito. A API é o centro do sistema: o front (React) só conversa com ela, só ela acessa o Oracle e só ela chama a IA (Flask) que prevê o prazo e o risco de atraso.

## Integrantes

| RM | Nome |
|---|---|
| 573982 | Leonardo Afonço Sousa |
| 569259 | Syang Cristina Gomes Souza |
| 573559 | Matheus Jones Sousa de Freitas |
| 569023 | Vitor Goncalves de Souza |
| 572364 | Joao Machado Nascimento |

## Links

- **API publicada (Render):** https://zelabairro-api-08ut.onrender.com
- **Repositório:** https://github.com/Leonardo-2112/zelabairro-api
- **Vídeo (YouTube, não listado, até 5 min):** https://youtu.be/oXqm0pAVw64
- **Teste rápido:** `GET https://zelabairro-api-08ut.onrender.com/health` → `{"status":"UP","banco":"UP"}`

## Stack

Java 21 · Spring Boot 4.1.2 (Spring MVC) · **JDBC puro** (`spring-boot-starter-jdbc` só para o DataSource/HikariCP; sem JPA, Hibernate ou Spring Data) · Oracle (`ojdbc17`) · ModelMapper 3.2.0 · Bean Validation · BCrypt (`spring-security-crypto`) · Maven Wrapper · Docker multi-stage (temurin 21 jdk → jre) · Render.

## Pacotes

```
br.com.zelabairro
├── model/       entidades do domínio (Cidadao, Gestor, Orgao, Bairro, Categoria, Ocorrencia, Atualizacao, Apoio, Foto)
│   └── enums/   StatusOcorrencia (RN04/RN05), Prioridade (RN10), Perfil
├── dao/         um DAO por tabela: JDBC com DataSource no construtor, PreparedStatement, try-with-resources, sq_tb_*.NEXTVAL
├── bo/          regras de negócio RNxx, controle de acesso (X-Gestor-Id / X-Cidadao-Id), transações e chamada à IA
├── dto/         DTOs de entrada (Bean Validation) e de saída; ErroResponseDto; conversor da ocorrência
├── controller/  rotas REST e status HTTP (201 + Location, 200, 204)
├── exception/   exceções próprias (404, 409, 401, 403, 400)
├── handler/     GlobalExceptionHandler (@RestControllerAdvice): todo erro no JSON padrão, sem stack trace nem SQL
└── config/      CORS (CORS_ORIGINS), ModelMapper, BCrypt
```

Caminho de uma requisição: **Controller** (recebe e valida o DTO com `@Valid`) → **ModelMapper** (DTO → model) → **BO** (regras RNxx) → **DAO** (SQL com `PreparedStatement`) → **Oracle**; na volta, model → DTO de saída. Qualquer exceção vai para o **handler**.

## Variáveis de ambiente

Nenhuma credencial fica no código, no `application.properties` ou no Git. A API lê tudo do ambiente:

| Variável | Obrigatória | Para quê |
|---|---|---|
| `DB_URL` | sim | URL JDBC do Oracle (formato `jdbc:oracle:thin:@//HOST:PORTA/SERVICO`) |
| `DB_USER` | sim | usuário do banco |
| `DB_PASSWORD` | sim | senha do banco |
| `CORS_ORIGINS` | sim | origens do front separadas por vírgula, ex.: `http://localhost:5173,https://SEU-FRONT.vercel.app` |
| `IA_API_URL` | não | URL da API de IA (Flask). Vazia ou fora do ar: a ocorrência nasce sem previsão (RN12) |
| `PORT` | não | porta HTTP (o Render define sozinho; padrão 8080) |
| `IA_TIMEOUT_MS`, `DB_POOL_SIZE` | não | timeout da IA (padrão 4000 ms) e tamanho do pool (padrão 4) |

Use o arquivo `.env.example` como modelo; o `.env` com os valores reais está no `.gitignore`.

## Como rodar

### 1. Banco
Execute o script `zelabairro.sql` (na raiz da entrega) no Oracle, num esquema sem as tabelas: no SQL Developer, abrir o arquivo e **Executar Script (F5)**. Ele cria as 9 tabelas, as sequences `sq_tb_*` e o seed (o gestor inicial, id 1, vem do seed — RN17).

### 2. API local (Linux/macOS)
```bash
cp .env.example .env        # preencha DB_URL, DB_USER, DB_PASSWORD, CORS_ORIGINS
set -a; . ./.env; set +a
./mvnw spring-boot:run
```
No Windows (PowerShell), defina as variáveis com `$env:DB_URL="..."` (e as demais) e rode `.\mvnw.cmd spring-boot:run`. Pela IDE: importe como projeto Maven e coloque as variáveis na Run Configuration.

Teste: `http://localhost:8080/health`.

### 3. Docker
```bash
docker build -t zelabairro-api .
docker run -p 8080:8080 --env-file .env zelabairro-api
```

### 4. Testar os endpoints
- **Postman/Insomnia:** importe `ZelaBairro.postman_collection.json` (raiz da entrega), troque a variável `baseUrl` e rode as pastas na ordem. A pasta **99 - Erros para a demo** traz 400, 401, 403, 404, 405 e 409.
- **Linha de comando:** `testes/testar_endpoints.sh URL_BASE` (curl + jq) confere 117 casos.

## Deploy no Render

1. Suba esta pasta (`zelabairro-api/`) para um repositório GitHub (pode ser privado).
2. No Render: **New → Web Service** → conecte o repositório → **Language: Docker** (o Render usa o `Dockerfile` da raiz).
3. Em **Environment**, crie `DB_URL`, `DB_USER`, `DB_PASSWORD`, `CORS_ORIGINS` e (opcional) `IA_API_URL`. Não crie `PORT`: o Render define.
4. Em **Advanced → Health Check Path**, coloque `/health`.
5. **Create Web Service** e espere o build (alguns minutos). Teste `https://SEU-SERVICO.onrender.com/health`.
6. Atualize `CORS_ORIGINS` com a URL da Vercel quando o front estiver publicado.

O plano gratuito "dorme" após ~15 min sem uso: abra `/health` alguns minutos antes de qualquer demonstração.

## Endpoints

`[G]` = exige o cabeçalho `X-Gestor-Id` de um gestor existente (RN16; senão 403). `[A]` = exige `X-Cidadao-Id` igual ao autor da ocorrência (RN18; senão 403).

| Método | Rota | Sucesso | Erros |
|---|---|---|---|
| POST | `/auth/login` | 200 `{id, nome, perfil}` | 400, 401 (RN15) |
| POST | `/cidadaos` | 201 + Location | 400, 409 (RN02) |
| GET | `/cidadaos` · `/cidadaos/{id}` | 200 | 404 |
| PUT · DELETE | `/cidadaos/{id}` | 200 · 204 | 400, 404, 409 |
| POST · GET · PUT · DELETE | `/gestores` · `/gestores/{id}` [G] | 201 · 200 · 200 · 204 | 400, 403, 404, 409 (RN17) |
| GET | `/orgaos` · `/orgaos/{id}` | 200 | 404 |
| POST · GET · PUT · DELETE | `/bairros` · `/bairros/{id}` | 201 · 200 · 200 · 204 | 400, 404, 409 (RN14) |
| GET | `/bairros/{id}/ocorrencias` | 200 | 404 |
| POST · GET · PUT · DELETE | `/categorias` · `/categorias/{id}` | 201 · 200 · 200 · 204 | 400, 404, 409 (RN14) |
| GET | `/categorias/{id}/ocorrencias` | 200 | 404 |
| POST | `/ocorrencias` | 201 + Location | 400, 404 |
| GET | `/ocorrencias?status=&idBairro=&idCategoria=` | 200 (por prioridade) | 400 |
| GET · PUT · DELETE | `/ocorrencias/{id}` | 200 · 200 · 204 | 400, 404, 409 (RN09) |
| GET | `/ocorrencias/{id}/atualizacoes` · `/{idAtualizacao}` | 200 | 404 |
| POST | `/ocorrencias/{id}/atualizacoes` [G] | 201 + Location | 400, 403, 404, 409 (RN05) |
| PUT | `/ocorrencias/{id}/atualizacoes/{idAtualizacao}` [G] | 200 | 400, 403, 404 |
| GET | `/ocorrencias/{id}/apoios` · `/{idCidadao}` | 200 | 404 |
| POST | `/ocorrencias/{id}/apoios` | 201 + Location | 400, 404, 409 (RN08) |
| DELETE | `/ocorrencias/{id}/apoios/{idCidadao}` | 204 | 404 |
| POST | `/ocorrencias/{id}/fotos` [A] (multipart, campo `arquivo`) | 201 + Location | 400, 403, 404, 409 (RN18) |
| GET | `/ocorrencias/{id}/fotos` · `/fotos/{idFoto}` | 200 | 404 |
| DELETE | `/ocorrencias/{id}/fotos/{idFoto}` [A] | 204 | 403, 404, 409 |
| GET | `/painel/bairros` [G] | 200 | 403 |
| GET | `/health` | 200 / 503 | — |

Formato de todo erro:
```json
{"status": 409, "erro": "Regra de negócio violada", "mensagem": "RN05: transição de ABERTA para RESOLVIDA não é permitida.",
 "caminho": "/ocorrencias/1/atualizacoes", "timestamp": "2026-11-18T01:37:38", "campos": []}
```

## Limitação assumida

Não há token: o front guarda o `id` devolvido no login e o envia no cabeçalho (`X-Gestor-Id` / `X-Cidadao-Id`), que a API confere no banco a cada requisição. Quem souber o id de um gestor consegue forjar o cabeçalho; para um MVP acadêmico é aceitável. Próxima etapa: JWT assinado com expiração.

## Uso de IA generativa

Ferramentas de IA generativa (Claude) foram usadas como apoio na escrita do código, dos testes e da documentação, sempre a partir das regras do Documento Base do grupo; todo o código foi revisado e testado pelo grupo, que sabe explicá-lo.
