# Cartório API

API REST para gerenciamento de cartórios, suas situações e atribuições.

## Tecnologias

- Java 11
- Spring Boot
- H2 Database
- Flyway
- Swagger/OpenAPI

## Requisitos

- JDK 11
- Maven

## Diagramas

### Entity-Relationship Diagram (ERD)

![ERD](/diagrams/erd.png)

### Classe

![Class](/diagrams/class.png)

## Executando a Aplicação

```bash
# Clone o repositório
git clone git@github.com:dluks82/escriba.git

# Entre no diretório
cd escriba

# Execute
./mvnw spring-boot:run
```

A aplicação estará disponível em `http://localhost:9564`

## Documentação API

Swagger UI: `http://localhost:9564/swagger-ui.html`

OpenAPI JSON: `http://localhost:9564/api-docs`

## Banco de Dados

H2 Console: `http://localhost:9564/h2-console`

```
URL: jdbc:h2:mem:cartoriodb
Usuario: sa
Senha: 
```

## Testes

```bash
# Todos os testes
./mvnw test

# Apenas testes unitários
./mvnw test -Dtest="*Test"

# Apenas testes de integração
./mvnw test -Dtest="*IT"
```

## Relatório de Cobertura

O relatório de cobertura de testes é gerado usando JaCoCo e pode ser acessado após executar:

```bash
# Gera relatório de cobertura
./mvnw clean verify

# O relatório estará disponível em:
target/site/jacoco/index.html
```

## Estrutura do Projeto

```
src/
├── main/
│   ├── java/
│   │   └── dev/dluks/escriba/
│   │       ├── domain/
│   │       │   ├── entities/
│   │       │   └── repositories/
│   │       ├── services/
│   │       └── controllers/
│   └── resources/
│       └── db/migration/
└── test/
    └── java/
        └── dev/dluks/escriba/
            ├── entities/
            ├── services/
            └── integration/
```

## Endpoints Principais

- `GET /api/v1/cartorios`: Lista cartórios
- `POST /api/v1/cartorios`: Cria cartório
- `GET /api/v1/situacoes`: Lista situações
- `GET /api/v1/atribuicoes`: Lista atribuições

