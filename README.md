# VetCare

API REST (Spring Boot 4 · Java 21 · PostgreSQL) para gestão de clínica veterinária: tutores, pets, veterinários, agenda e consultas.

## Como rodar

**Backend** (`server/`)

1. Crie o banco PostgreSQL e copie `server/.env.example` para `server/.env` (ajuste os valores). O `.env` **não** deve ser commitado.
2. `JWT_SECRET` é obrigatório e precisa ter **32+ caracteres** (`openssl rand -base64 48`). Sem ele a aplicação não sobe.
3. `ADMIN_EMAIL` / `ADMIN_SENHA` criam o **primeiro administrador** na primeira inicialização (só se ainda não existir nenhum).
4. Opcional, só em desenvolvimento: `SEED_ENABLED=true` + `SEED_SENHA=<senha>` criam veterinários, tutores, pets e agenda de exemplo (todos com a senha definida em `SEED_SENHA`).
5. `./mvnw spring-boot:run` (testes: `./mvnw test`).

**Frontend** (`client/`)

1. `npm install` e `npm run dev` (porta 5173). O Vite faz proxy de `/api` para `http://localhost:8080`.
2. Em produção, defina `VITE_API_URL` com a URL da API.
3. O acesso é feito pela tela de login com um usuário da equipe (ADMIN, RECEPCIONISTA ou VETERINARIO). O perfil vem do token. Com o seed ativo, os logins de exemplo são `recepcao@vetcare.com`, `camila@vetcare.com` e `rafael@vetcare.com` (senha = `SEED_SENHA`).

## Autenticação (JWT)

- `POST /api/auth/login` → devolve `token` (Bearer, HS256, expira em `JWT_EXPIRATION_MINUTES`).
- Envie `Authorization: Bearer <token>` nas demais rotas.
- O token carrega `sub` (id), `email`, `nome` e `cargo`. **O usuário e o perfil vêm sempre do token**, nunca de parâmetros da requisição.
- Perfis: `ADMIN`, `RECEPCIONISTA`, `VETERINARIO`, `TUTOR`.
- **RECEPCIONISTA**: opera o balcão (cadastra tutores e pets, abre horários e agenda/cancela consultas em nome do tutor). **Não** registra atendimento e **não vê** diagnóstico, prescrição e observações clínicas das consultas.

Erros seguem o formato `{ timestamp, status, erro, mensagem, caminho, campos }`
(400 validação · 401 sem/ token inválido · 403 sem permissão · 404 · 409 conflito · 422 regra de negócio).

## Rotas

| Método | Rota | Quem | Descrição |
|---|---|---|---|
| POST | `/api/auth/login` | público | Login |
| POST | `/api/auth/registro` | público | Autocadastro de tutor |
| GET | `/api/auth/me` | logado | Dados do usuário logado |
| PATCH | `/api/auth/senha` | logado | Alterar senha |
| GET | `/api/usuarios` | ADMIN | Lista usuários |
| GET/PUT | `/api/usuarios/{id}` | ADMIN ou o próprio | Ver / editar nome e telefone |
| PATCH | `/api/usuarios/{id}/status` | ADMIN | Ativar/desativar |
| POST/GET | `/api/administradores` | ADMIN | Cadastrar / listar admins |
| POST/GET | `/api/recepcionistas` | ADMIN | Cadastrar / listar recepcionistas |
| POST | `/api/tutores` | ADMIN, RECEPCIONISTA | Cadastrar tutor |
| GET | `/api/tutores` | ADMIN, RECEPCIONISTA, VET | Listar tutores |
| GET | `/api/tutores/{id}` | ADMIN, VET, o próprio | Ver tutor |
| POST | `/api/veterinarios` | ADMIN | Cadastrar veterinário |
| GET | `/api/veterinarios`, `/{id}` | logado | Listar / ver (não-admin vê só ativos) |
| PUT | `/api/veterinarios/{id}` | ADMIN ou o próprio | Atualizar |
| POST | `/api/pets` | TUTOR, ADMIN, RECEPCIONISTA | Cadastrar pet (ADMIN/RECEPCIONISTA informam `tutorId`) |
| GET | `/api/pets` | logado | TUTOR: os seus · demais: todos |
| GET | `/api/pets/{id}`, `/api/pets/tutor/{tutorId}` | dono, ADMIN, RECEPCIONISTA, VET | Consultar |
| PUT/DELETE | `/api/pets/{id}` | dono, ADMIN, RECEPCIONISTA | Atualizar / excluir (só ADMIN transfere de tutor) |
| POST | `/api/agendas` | VET, ADMIN, RECEPCIONISTA | Abre vários horários em uma data (ADMIN/RECEPCIONISTA informam `veterinarioId`) |
| GET | `/api/agendas/veterinario/{id}?data=AAAA-MM-DD&apenasDisponiveis=true` | logado | Horários do veterinário |
| DELETE | `/api/agendas/{id}` | VET dono, ADMIN, RECEPCIONISTA | Remove horário |
| POST | `/api/consultas` | TUTOR, ADMIN, RECEPCIONISTA | Agenda consulta (`agendaId`, `petId`, `motivo`; ADMIN/RECEPCIONISTA informam `tutorId`) |
| GET | `/api/consultas?status=` | logado | TUTOR/VET: as suas · ADMIN/RECEPCIONISTA: todas (recepção sem dados clínicos) |
| GET | `/api/consultas/agenda?data=` | VET, ADMIN, RECEPCIONISTA (`&profissionalId=`) | Agenda do dia |
| GET | `/api/consultas/{id}` | participantes, ADMIN | Detalhe |
| PATCH | `/api/consultas/{id}/realizar` | VET responsável | Registra diagnóstico/prescrição |
| PATCH | `/api/consultas/{id}/cancelar` | tutor dono, VET responsável, ADMIN, RECEPCIONISTA | Cancela |

Fluxo típico: veterinário abre horários → tutor lista `GET /api/agendas/veterinario/{id}?data=...` → `POST /api/consultas` com o `id` do horário.

## Regras de negócio (nos services)

- **Cadastro**: e-mail (minúsculo) e CPF únicos; CPF validado pelos dígitos verificadores; senha com BCrypt (8–72 caracteres); CRMV único.
- **Login**: mensagem única para e-mail/senha errados; usuário inativo não entra.
- **Usuários**: ninguém desativa a própria conta; veterinário/tutor com consultas agendadas não pode ser desativado.
- **Pets**: sempre pertencem a um tutor ativo; só o dono (ou ADMIN) altera/exclui; só ADMIN transfere de tutor; pet com consultas não pode ser excluído.
- **Agenda**: sem datas/horários passados; sem horário duplicado; intervalo mínimo = duração da consulta (30 min); horário com consulta vinculada não pode ser removido.
- **Agendar consulta**: o pet deve ser do tutor; horário precisa estar disponível e no futuro (reserva com lock pessimista contra corrida); o pet não pode ter outra consulta no mesmo período; reservar bloqueia o horário.
- **Cancelar**: só consultas `AGENDADA` antes do início; tutor precisa de **2h de antecedência** (recepção/ADMIN/veterinário não têm essa exigência); se cancelar o tutor/ADMIN o horário volta a ficar livre, se cancelar o veterinário o horário permanece bloqueado.
- **Realizar**: só o veterinário responsável, só após o horário de início, diagnóstico obrigatório, uma única vez.

Parâmetros configuráveis em `application.properties` (`app.consulta.duracao-minutos`, `app.consulta.antecedencia-cancelamento-horas`, `app.timezone`).

## Estrutura

```
server/src/main/java/com/backend/vetcare
├── config/       SecurityConfig, JwtConfig, AppConfig (Clock), AdminInicializador
├── security/     JwtService, UsuarioAutenticado
├── controller/   rotas REST
├── service/      regras de negócio
├── repositories/ Spring Data JPA
├── model/        entidades
├── dto/          request / response
├── exception/    exceções de negócio + GlobalExceptionHandler
└── util/         CpfUtil
```
