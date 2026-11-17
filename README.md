# VetCare — Sistema de Gestão para Clínica Veterinária

Projeto desenvolvido para a disciplina **Engenharia de Software 1** — Instituto de Computação (UFBA), sob orientação da **Profa. Dra. Larissa Barbosa Leôncio Pinheiro**.

## Equipe
- Gabriel Brito
- Pedro Wenzel
- Pedro França
- Samuel Marques

---

## Visão Geral e Arquitetura

O **VetCare** adota o estilo arquitetural de **Monolito Modular em Camadas** no backend (Spring Boot + Java 21 + PostgreSQL) integrado a uma **Single Page Application (SPA)** no frontend (React + TypeScript + Vite), conforme documentado no Trabalho I (Etapa 1).

### Estrutura do Repositório

```text
VetCare-Project/
├── client/          # Frontend SPA (React + TypeScript + Vite)
│   ├── src/
│   │   ├── components/   # Sidebar (RBAC) e Header com busca incremental
│   │   ├── pages/        # Telas: Agenda, Atendimento, Pacientes e Financeiro
│   │   ├── services/     # Integração com a API REST do Backend
│   │   └── types/        # Tipagens de domínio (Pet, Tutor, Consulta, Prontuário, Pagamento)
│   └── package.json
├── server/          # Backend API REST (Spring Boot + Spring Security + JPA)
│   ├── src/main/java/com/backend/vetcare/
│   │   ├── config/       # Configuração de Segurança e BCrypt
│   │   ├── controller/   # Endpoints REST (/auth, /pets, /consultas, /api/agendas, /medicos, etc.)
│   │   ├── dto/          # DTOs de requisição e resposta
│   │   ├── model/        # Entidades JPA (Usuario, Tutor, Veterinario, Administrador, Pet, Consulta, Agenda)
│   │   ├── repositories/ # Repositórios Spring Data JPA
│   │   └── service/      # Regras de negócio e validações
│   └── pom.xml
└── docs/            # Documentação arquitetural, ADRs e entregáveis da disciplina
```

---

## Pré-requisitos

- **Java JDK 21** (ou superior)
- **Maven** (ou uso do wrapper `./mvnw` incluso na pasta `server/`)
- **PostgreSQL** (para persistência relacional do backend)
- **Node.js 18+** e **npm** (para executar o frontend)

---

## Como Rodar o Sistema

### 1. Executando o Frontend (`client/`)

Abra um terminal na raiz do repositório e execute:

```bash
cd client
npm install
npm run dev
```

O frontend ficará disponível em: **`http://localhost:5173`**

### 2. Executando o Backend (`server/`)

1. Certifique-se de que o PostgreSQL está em execução e configure as variáveis/credenciais em `server/src/main/resources/application.properties` (`DB_URL`, `DB_USER`, `DB_PASSWORD`).
2. Abra um terminal na pasta `server/` e execute:

```bash
cd server
./mvnw spring-boot:run
```
*(No Windows PowerShell/CMD, utilize `.\mvnw.cmd spring-boot:run`)*

A API REST ficará disponível em: **`http://localhost:8080`**
