# FreteMais - Driver Manager

Sistema de gerenciamento de motoristas desenvolvido como teste técnico. Uma aplicação full-stack para cadastro, consulta e gerenciamento de motoristas com foco em performance, escalabilidade e boas práticas.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-blue.svg)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-Ready-blue.svg)](https://www.docker.com/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## Índice

- [Sobre o Projeto](#sobre-o-projeto)
- [Funcionalidades](#funcionalidades)
- [Arquitetura](#arquitetura)
- [Tecnologias Utilizadas](#tecnologias-utilizadas)
- [Pré-requisitos](#pré-requisitos)
- [Instalação e Execução](#instalação-e-execução)
- [Documentação da API](#documentação-da-api)
- [Estrutura do Projeto](#estrutura-do-projeto)
- [Testes](#testes)
- [Otimizações de Performance](#otimizações-de-performance)
- [Autor](#autor)

---

## Sobre o Projeto

O **Driver Manager** é uma API REST robusta para gerenciamento de motoristas, desenvolvida com **Spring Boot 4.0.2** e **Java 21**. O projeto demonstra conhecimentos em:

- Arquitetura em camadas (Domain, Application, Infrastructure)
- Clean Code e princípios SOLID
- Otimizações de performance (Virtual Threads, ZGC, CDS)
- Containerização com Docker
- Testes automatizados
- Segurança com Spring Security e JWT
- Documentação OpenAPI/Swagger

---

## Funcionalidades

### Autenticação e Segurança
- Autenticação JWT
- Autorização baseada em roles
- Filtros de segurança customizados

### Gerenciamento de Motoristas
- **Criar** novo motorista
- **Listar** motoristas com paginação e filtros
  - Filtro por texto (nome, email, CPF, CNH)
  - Filtro por localização (estado, cidade)
  - Filtro por tipos de veículos
- **Consultar** motorista por ID
- **Atualizar** dados do motorista
- **Excluir** motorista

### Recursos Adicionais
- Validação de CPF brasileiro
- Suporte a múltiplos tipos de veículos por motorista
- Controle de disponibilidade
- Busca com especificações JPA
- Paginação e ordenação

---

## Arquitetura

O projeto segue a arquitetura em camadas (Layered Architecture):

```
┌─────────────────────────────────────────┐
│         Infrastructure Layer            │
│  (Controllers, Security, Exceptions)    │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│         Application Layer               │
│    (Services, DTOs, Mappers)            │
└─────────────────┬───────────────────────┘
                  │
┌─────────────────▼───────────────────────┐
│           Domain Layer                  │
│      (Entities, Enums, Rules)           │
└─────────────────────────────────────────┘
```

### Camadas

- **Domain**: Entidades de negócio (`Driver`, `VehicleType`)
- **Application**: Lógica de aplicação, DTOs e mapeadores
- **Infrastructure**: Adaptadores externos (controllers, repositories, security)

---

## Tecnologias Utilizadas

### Backend
- **Java 21** - Última LTS com Virtual Threads (Project Loom)
- **Spring Boot 4.0.2** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Autenticação e autorização
- **Spring Validation** - Validação de dados
- **Hibernate Validator** - Validação customizada (CPF)

### Banco de Dados
- **PostgreSQL 16** - Banco de dados principal (produção)
- **H2 Database** - Banco em memória (desenvolvimento/testes)

### Documentação
- **SpringDoc OpenAPI** - Documentação Swagger/OpenAPI 3

### Build e Deploy
- **Maven** - Gerenciador de dependências
- **Docker** - Containerização
- **Docker Compose** - Orquestração de containers

### Qualidade e Performance
- **Lombok** - Redução de boilerplate
- **JUnit 5** - Testes unitários
- **MockMvc** - Testes de integração
- **ZGC** - Garbage Collector de baixa latência
- **CDS (Class Data Sharing)** - Otimização de startup
- **Virtual Threads** - Concorrência escalável

---

## Pré-requisitos

Antes de começar, certifique-se de ter instalado:

- **Docker** e **Docker Compose** (recomendado)
  
  OU

- **Java 21** ou superior
- **Maven 3.9** ou superior
- **PostgreSQL 16** (se não usar Docker)

---

## Instalação e Execução

### Opção 1: Docker Compose (Recomendado)

1. Clone o repositório:
```bash
git clone https://github.com/vitinh0z/fretemais-driver-manager.git
cd fretemais-driver-manager
```

2. Execute com Docker Compose:
```bash
docker-compose up -d
```

3. Aguarde a inicialização (cerca de 40 segundos)

4. Acesse a aplicação:
   - API: http://localhost:8080
   - Swagger UI: http://localhost:8080/swagger-ui.html
   - Health Check: http://localhost:8080/actuator/health

### Opção 2: Executar Localmente

1. Clone e configure o banco de dados:
```bash
git clone https://github.com/vitinh0z/fretemais-driver-manager.git
cd fretemais-driver-manager/backend/driver-manager
```

2. Configure as variáveis de ambiente ou edite `application-dev.yml`

3. Execute a aplicação:
```bash
./mvnw spring-boot:run
```

### Opção 3: Build Manual

```bash
cd backend/driver-manager
./mvnw clean package
java -jar target/driver-manager-0.0.1-SNAPSHOT.jar
```

---

## Documentação da API

A documentação interativa está disponível via **Swagger UI**:

**URL**: http://localhost:8080/swagger-ui.html

### Endpoints Principais

| Método | Endpoint | Descrição |
|--------|----------|-----------|
| `POST` | `/api/drivers` | Criar novo motorista |
| `GET` | `/api/drivers` | Listar motoristas (com filtros) |
| `GET` | `/api/drivers/{id}` | Buscar motorista por ID |
| `PUT` | `/api/drivers/{id}` | Atualizar motorista |
| `DELETE` | `/api/drivers/{id}` | Excluir motorista |

### Exemplo de Request

```json
{
  "name": "João Silva",
  "email": "joao.silva@email.com",
  "phone": "11999999999",
  "cpf": "52998224725",
  "cnh": "12345678900",
  "city": "São Paulo",
  "state": "SP",
  "vehicleTypes": ["CAR", "MOTORCYCLE"]
}
```

### Tipos de Veículos Suportados

- `CAR` - Carro
- `MOTORCYCLE` - Moto
- `TRUCK` - Caminhão
- `VAN` - Van

---

## Estrutura do Projeto

```
fretemais-driver-manager/
├── backend/
│   └── driver-manager/
│       ├── src/
│       │   ├── main/
│       │   │   ├── java/com/fretemais/drivermanager/
│       │   │   │   ├── application/
│       │   │   │   │   ├── dtos/
│       │   │   │   │   ├── mappers/
│       │   │   │   │   └── services/
│       │   │   │   ├── domain/
│       │   │   │   │   ├── enums/
│       │   │   │   │   └── model/
│       │   │   │   ├── infrastructure/
│       │   │   │   │   ├── controllers/
│       │   │   │   │   ├── exceptions/
│       │   │   │   │   ├── persistence/
│       │   │   │   │   └── security/
│       │   │   │   └── config/
│       │   │   └── resources/
│       │   │       ├── application.yml
│       │   │       ├── application-dev.yml
│       │   │       └── application-prod.yml
│       │   └── test/
│       └── pom.xml
├── frontend/ (em desenvolvimento)
├── docker-compose.yml
├── Dockerfile
└── README.md
```

---

## Testes

### Executar Testes

```bash
cd backend/driver-manager
./mvnw test
```

### Cobertura de Testes

O projeto inclui:
- Testes unitários de serviços
- Testes de integração de controllers
- Testes de repositórios JPA
- Testes de validação de DTOs

---

## Otimizações de Performance

### 1. Virtual Threads (Project Loom)
```yaml
spring:
  threads:
    virtual:
      enabled: true
```
Melhora a escalabilidade para operações I/O-bound.

### 2. ZGC (Z Garbage Collector)
```dockerfile
-XX:+UseZGC
-XX:+ZGenerational
```
Garbage collector de baixa latência para pausas menores que 10ms.

### 3. CDS (Class Data Sharing)
Pré-carregamento de classes compartilhadas para startup 30% mais rápido.

### 4. AOT Compilation
```xml
-Dspring-boot.aot.enabled=true
```
Compilação ahead-of-time para melhor performance inicial.

### 5. Connection Pooling (HikariCP)
```yaml
hikari:
  maximum-pool-size: 20
  minimum-idle: 5
  idle-timeout: 30000
```

### 6. PostgreSQL Tuning
Configurações otimizadas para performance em `docker-compose.yml`.

---

## Docker

### Recursos Alocados

**Aplicação:**
- CPU: 0.5-2 cores
- Memória: 512MB-1GB

**PostgreSQL:**
- CPU: 0.25-1 core
- Memória: 256MB-512MB

### Health Checks

- **App**: `http://localhost:8080/actuator/health`
- **Database**: `pg_isready -U postgres -d drivermanager`

---

## Segurança

- Autenticação JWT com tokens seguros
- Senhas hasheadas com BCrypt
- Proteção CSRF habilitada
- Usuário não-root em container Docker
- Validação de dados de entrada
- Tratamento global de exceções

---

## Autor

**Victor Gabriel**

- GitHub: [@vitinh0z](https://github.com/vitinh0z)

---

## Licença

Este projeto está sob a licença MIT. Veja o arquivo [LICENSE](LICENSE) para mais detalhes.

---

## Considerações Finais

Este projeto foi desenvolvido como teste técnico, demonstrando:

- Conhecimento sólido em Spring Boot e ecossistema Java
- Boas práticas de Clean Code e arquitetura
- Expertise em otimização de performance
- Domínio de Docker e containerização
- Capacidade de documentação técnica
- Atenção a segurança e escalabilidade

---

<div align="center">
  Desenvolvido por <a href="https://github.com/vitinh0z">Victor Gabriel</a>
</div>
