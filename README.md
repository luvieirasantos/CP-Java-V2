# Ferramentas API — CP Spring Boot (Oracle + HATEOAS)

![Java](https://img.shields.io/badge/Java-21-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.8-brightgreen) ![Build](https://img.shields.io/badge/build-Maven-orange)
 
API REST de **Ferramentas** desenvolvida para o **Checkpoint (CP)**, com:
- **Oracle** como banco de dados
- **CRUD completo** com **Spring Data JPA**
- **HATEOAS** (links de navegação, nível 3)
- **Validações** (`jakarta.validation`)
- **Lombok**
- Porta **8081**

> As seções abaixo mostram como rodar, como está a modelagem, todos os endpoints com _payloads_ de exemplo, prints do Postman/Insomnia e troubleshooting.
 
---

## Sumário
- [Arquitetura & Stack](#arquitetura--stack)
- [Requisitos](#requisitos)
- [Configuração do Banco (Oracle)](#configuração-do-banco-oracle)
- [Como rodar localmente](#como-rodar-localmente)
- [Modelagem & DDL](#modelagem--ddl)
- [Estrutura de pacotes](#estrutura-de-pacotes)
- [Endpoints & Exemplos](#endpoints--exemplos)
- [HATEOAS](#hateoas)
- [Capturas de tela](#capturas-de-tela)
- [Deploy (opcional)](#deploy-opcional)
- [Troubleshooting](#troubleshooting)
- [Créditos](#créditos)
 
---

## Arquitetura & Stack
- **Linguagem:** Java 21
- **Framework:** Spring Boot 3.4.8
- **Módulos:** Web, Data JPA, Validation, HATEOAS, Lombok, Oracle JDBC
- **Persistência:** Oracle (FIAP/Oracle Cloud)
- **Padrões:** DTO (entrada/saída), Service, Repository, Controller, HATEOAS Assembler
- **Observabilidade:** `spring.jpa.show-sql=true` para ajudar no debug

Estrutura geral (camadas):
```
Controller  →  Service  →  Repository (JPA)  →  Oracle
               ↑
            DTOs, Validation
```
 
---

## Requisitos
- **JDK 21** (recomendado). Funciona com JDK 24, mas os avisos de native access do IntelliJ podem aparecer.
- **Maven 3.9+**
- Acesso a um **Oracle** (FIAP / Autonomous / XE local).

---

## Configuração do Banco (Oracle)
Defina o `application.properties` (ou `.yml`) com sua conexão Oracle. Exemplo:

```properties
server.port=8081
spring.application.name=ferramentas

spring.datasource.url=jdbc:oracle:thin:@//HOST:1521/SERVICENAME
spring.datasource.username=USUARIO
spring.datasource.password=SENHA
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=none
spring.jpa.database-platform=org.hibernate.dialect.OracleDialect
spring.jpa.show-sql=true

# Controle dos scripts schema.sql / data.sql
# always: executa a cada boot | never: não executa
spring.sql.init.mode=never
# Se quiser que ignore erros de 'já existe' (ORA-00955), habilite:
# spring.sql.init.continue-on-error=true
```

> **Dica:** Em muitos ambientes Oracle modernos, usa-se **Service Name** e não **SID**. O formato `//host:porta/servicename` é o mais comum.

---

## Como rodar localmente
1. Configure o `application.properties` com sua URL/usuário/senha do Oracle.
2. (Opcional) Rode os scripts de DDL manualmente no SQL Developer (veja [Modelagem & DDL](#modelagem--ddl)).
3. Compile e execute:
   ```bash
   mvn clean spring-boot:run
   ```
4. A API sobe em: `http://localhost:8081`  
   Teste `GET /ferramentas`.

**Executar JAR:**
```bash
mvn clean package
java -jar target/ferramentas-0.0.1-SNAPSHOT.jar
```

---

## Modelagem & DDL
Entidade: **Ferramenta** → Tabela `TDS_TB_FERRAMENTAS`

| Campo         | Tipo           | Obrigatório |
|---------------|----------------|-------------|
| `ID`          | NUMBER(10)     | Sim (PK)    |
| `NOME`        | VARCHAR2(100)  | Sim         |
| `TIPO`        | VARCHAR2(50)   | Não         |
| `CLASSIFICACAO` | VARCHAR2(50) | Não         |
| `TAMANHO`     | VARCHAR2(50)   | Não         |
| `PRECO`       | NUMBER(10,2)   | Sim         |

**DDL (schema.sql):**
```sql
CREATE TABLE TDS_TB_FERRAMENTAS (
  ID            NUMBER(10)      PRIMARY KEY,
  NOME          VARCHAR2(100)   NOT NULL,
  TIPO          VARCHAR2(50),
  CLASSIFICACAO VARCHAR2(50),
  TAMANHO       VARCHAR2(50),
  PRECO         NUMBER(10,2)    NOT NULL
);

CREATE SEQUENCE TDS_SEQ_FERRAMENTAS START WITH 1 INCREMENT BY 1 NOCACHE;
```

> A entidade usa `@SequenceGenerator(name = "seqFerramentas", sequenceName = "TDS_SEQ_FERRAMENTAS", allocationSize = 1)`.

**Seeds (data.sql – opcional):**
```sql
INSERT INTO TDS_TB_FERRAMENTAS (ID, NOME, TIPO, CLASSIFICACAO, TAMANHO, PRECO)
VALUES (TDS_SEQ_FERRAMENTAS.NEXTVAL, 'Martelo', 'Manual', 'Uso geral', 'Médio', 29.90);
```

> Depois de popular uma vez, defina `spring.sql.init.mode=never` para não duplicar dados a cada start.

---

## Estrutura de pacotes
```
br.com.fiap
└── ferraments (ex.: br.com.fiap.ferramentas)
    ├── FerramentasApplication.java
    ├── domain
    │   ├── entity
    │   │   └── Ferramenta.java
    │   └── dto
    │       ├── FerramentaCreateDTO.java
    │       ├── FerramentaUpdateDTO.java
    │       └── FerramentaOutputDTO.java   (@Relation item/collection)
    ├── repository
    │   └── FerramentaRepository.java
    ├── service
    │   └── FerramentaService.java
    ├── api
    │   └── FerramentaModelAssembler.java  (HATEOAS)
    ├── web
    │   └── FerramentaController.java
    └── exception (opcional)
        └── ApiExceptionHandler.java
```

---

## Endpoints & Exemplos

> **Base URL:** `http://localhost:8081`

### Listar (GET `/ferramentas?page=0&size=10`)
- **200 OK**
- Corpo (com `@Relation` no DTO, coleção renomeada):  
```json
{
  "_embedded": {
    "ferramentas": [
      {
        "id": 1,
        "nome": "Martelo",
        "tipo": "Manual",
        "classificacao": "Uso geral",
        "tamanho": "Médio",
        "preco": 29.9,
        "_links": {
          "self": { "href": "http://localhost:8081/ferramentas/1" },
          "collection": { "href": "http://localhost:8081/ferramentas?page=0&size=10" },
          "update": { "href": "http://localhost:8081/ferramentas/1" },
          "delete": { "href": "http://localhost:8081/ferramentas/1" }
        }
      }
    ]
  },
  "_links": {
    "self": { "href": "http://localhost:8081/ferramentas?page=0&size=10" }
  }
}
```

**cURL:**
```bash
curl -X GET "http://localhost:8081/ferramentas?page=0&size=10"
```

> **Paginação avançada (opcional):** se usar `PagedResourcesAssembler`, a resposta inclui links `first/prev/next/last` e o bloco `page`.

### Buscar por ID (GET `/ferramentas/{id}`)
- **200 OK** | **404 Not Found**  
**cURL:**
```bash
curl -X GET "http://localhost:8081/ferramentas/1"
```

### Criar (POST `/ferramentas`)
- **201 Created** + Header `Location: /ferramentas/{id}`
- Exemplo de corpo de **requisição**:
```json
{
  "nome": "Serra",
  "tipo": "Manual",
  "classificacao": "Uso geral",
  "tamanho": "Pequeno",
  "preco": 15.75
}
```
- Resposta (exemplo real obtido via Postman):  
```json
{
  "id": 5,
  "nome": "Serra",
  "tipo": "Manual",
  "classificacao": "Uso geral",
  "tamanho": "Pequeno",
  "preco": 15.75,
  "_links": {
    "self": { "href": "http://localhost:8081/ferramentas/5" },
    "collection": { "href": "http://localhost:8081/ferramentas?page=0&size=10" },
    "update": { "href": "http://localhost:8081/ferramentas/5" },
    "delete": { "href": "http://localhost:8081/ferramentas/5" }
  }
}
```
**cURL:**
```bash
curl -X POST "http://localhost:8081/ferramentas"   -H "Content-Type: application/json"   -d '{"nome":"Serra","tipo":"Manual","classificacao":"Uso geral","tamanho":"Pequeno","preco":15.75}'
```

### Atualizar (PUT `/ferramentas/{id}`)
- **200 OK** | **404 Not Found**
```json
{
  "nome": "Chave Phillips",
  "tipo": "Manual",
  "classificacao": "Uso geral",
  "tamanho": "Médio",
  "preco": 18.90
}
```
**cURL:**
```bash
curl -X PUT "http://localhost:8081/ferramentas/5"   -H "Content-Type: application/json"   -d '{"nome":"Chave Phillips","tipo":"Manual","classificacao":"Uso geral","tamanho":"Médio","preco":18.90}'
```

### Atualização parcial (PATCH `/ferramentas/{id}`)
- **200 OK** | **404 Not Found**
```json
{
  "preco": 17.90,
  "tamanho": "Pequeno"
}
```
**cURL:**
```bash
curl -X PATCH "http://localhost:8081/ferramentas/5"   -H "Content-Type: application/json"   -d '{"preco":17.90,"tamanho":"Pequeno"}'
```

### Excluir (DELETE `/ferramentas/{id}`)
- **204 No Content** | **404 Not Found**
**cURL:**
```bash
curl -X DELETE "http://localhost:8081/ferramentas/5"
```

---

## HATEOAS
- **Item (Ferramenta)** retorna links:
  - `self`: o recurso atual (`/ferramentas/{id}`)
  - `collection`: volta à coleção (`/ferramentas?page=..&size=..`)
  - `update`: sugere atualização do recurso atual
  - `delete`: sugere exclusão do recurso atual
- **Coleção** retorna ao menos `self` e, se usar `PagedResourcesAssembler`, também `first/prev/next/last` + bloco `page`.

**Assembler** (exemplo de self/collection/update/delete):
```java
return EntityModel.of(dto,
  linkTo(methodOn(FerramentaController.class).getById(entidade.getId())).withSelfRel(),
  linkTo(methodOn(FerramentaController.class).list(0, 10)).withRel(IanaLinkRelations.COLLECTION),
  linkTo(methodOn(FerramentaController.class).update(entidade.getId(), null)).withRel("update"),
  linkTo(methodOn(FerramentaController.class).delete(entidade.getId())).withRel("delete")
);
```

---

## Capturas de tela
Coloque as imagens no repositório em `docs/img/` e referencie aqui. Se preferir, substitua pelos seus próprios arquivos/nomes:

- **Spring Initializr**  
  `docs/img/initializr.png`
- **GET /ferramentas** (HATEOAS)  
  `docs/img/get-ferramentas.png`
- **POST /ferramentas** (201 Created)  
  `docs/img/post-ferramentas-created.png`

---

## Deploy (opcional)
**Empacotamento:**
```bash
mvn -DskipTests clean package
```
Gera o JAR em `target/`. Configure as variáveis de ambiente do Oracle no provedor (Render/Railway/Fly.io) e rode `java -jar`.

**Dockerfile** (exemplo simples):
```dockerfile
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY target/ferramentas-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8081
ENTRYPOINT ["java","-jar","/app/app.jar"]
```

**Build & Run:**
```bash
docker build -t ferramentas:cp .
docker run -p 8081:8081 --env-file .env ferramentas:cp
```

`.env` (exemplo):
```
SPRING_DATASOURCE_URL=jdbc:oracle:thin:@//HOST:1521/SERVICE
SPRING_DATASOURCE_USERNAME=USUARIO
SPRING_DATASOURCE_PASSWORD=SENHA
SPRING_JPA_HIBERNATE_DDL_AUTO=none
SPRING_SQL_INIT_MODE=never
```

---

## Troubleshooting

### ORA-00955: nome já está sendo usado por um objeto existente
Acontece quando `schema.sql` tenta criar algo que já existe.
- **Solução 1:** `spring.sql.init.mode=never` (não rodar scripts em cada start)
- **Solução 2:** `spring.sql.init.continue-on-error=true` (ignorar erros de “já existe”)
- **Solução 3:** dropar manualmente e subir de novo:
  ```sql
  DROP TABLE TDS_TB_FERRAMENTAS CASCADE CONSTRAINTS;
  DROP SEQUENCE TDS_SEQ_FERRAMENTAS;
  ```

### Subi “Hello world!” e a API não ficou ouvindo
Você executou a `main` errada. Rode a classe do Spring Boot:
`FerramentasApplication` (com `@SpringBootApplication`).

### Erro no Assembler (list null,int,int)
Assinatura do método `list` recebe **dois ints**:
```java
linkTo(methodOn(FerramentaController.class).list(0, 10)).withRel(IanaLinkRelations.COLLECTION);
```

### Porta diferente de 8081
Confirme `server.port=8081` em `application.properties`.

### JDK 24 - aviso de native access
Apenas um aviso do IntelliJ. Recomenda-se **JDK 21** para Spring Boot 3.4.x.

---

## Créditos
- **Autor(a):** Lu Vieira Santos
- **Disciplina:** CP Spring Boot — FIAP
- **Descrição:** API de Ferramentas (Oracle + HATEOAS), CRUD completo, documentação e prints.

---

> Qualquer dúvida ou ajuste fino (ex.: paginação HATEOAS com `PagedResourcesAssembler`, políticas de erro com `@RestControllerAdvice`, ou Docker Compose com Oracle XE), abra uma issue ou entre em contato.
