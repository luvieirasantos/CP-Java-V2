# Ferramentas API — CP Spring Boot (Oracle + HATEOAS)

![Java](https://img.shields.io/badge/Java-21-blue) ![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.8-brightgreen) ![Build](https://img.shields.io/badge/build-Maven-orange)

API REST de **Ferramentas** (CRUD + **HATEOAS**) com **Oracle**. Este repositório/entrega corresponde ao **Checkpoint (CP)**.

> **Live:** https://cp-java-v2.onrender.com  
> Exemplos: `https://cp-java-v2.onrender.com/ferramentas`

---

## Sumário
- [Arquitetura & Stack](#arquitetura--stack)
- [Como usar a API publicada](#como-usar-a-api-publicada)
- [Coleção do Postman](#coleção-do-postman)
- [Endpoints & Exemplos](#endpoints--exemplos)
- [HATEOAS](#hateoas)
- [Rodar localmente (sem Render)](#rodar-localmente-sem-render)
- [Deploy no Render (como foi feito)](#deploy-no-render-como-foi-feito)
- [Troubleshooting](#troubleshooting)
- [Créditos](#créditos)

---

## Arquitetura & Stack
- **Spring Boot 3.4.8** (Web, Data JPA, Validation, HATEOAS)
- **Lombok** para reduzir boilerplate
- **Oracle** como banco relacional
- **Java 21**, **Maven**
- **HATEOAS** para navegação por links
- **Deploy:** Render (Dockerfile multi-stage)

---

## Como usar a API publicada
Base URL pública:
```
https://cp-java-v2.onrender.com
```

Exemplos rápidos (**cURL**):

```bash
curl -s "https://cp-java-v2.onrender.com/ferramentas?page=0&size=10"

curl -s -X POST "https://cp-java-v2.onrender.com/ferramentas"   -H "Content-Type: application/json"   -d '{"nome":"Serra","tipo":"Manual","classificacao":"Uso geral","tamanho":"Pequeno","preco":15.75}'
```

> Caso a API esteja temporariamente hibernada pelo Render, a **primeira requisição** pode demorar alguns segundos para acordar o serviço.

---

## Coleção do Postman
Baixe/importar a coleção com todos os endpoints. A variável `baseUrl` já está apontada para o ambiente publicado.

- **Ferramentas.postman_collection.json** → [download](sandbox:/mnt/data/postman/Ferramentas.postman_collection_render.json)

Se quiser usar localmente, altere a variável `baseUrl` para `http://localhost:8081` dentro do Postman.

---

## Endpoints & Exemplos

> **Base:** `https://cp-java-v2.onrender.com` (ou `http://localhost:8081` localmente)

### GET `/ferramentas?page=0&size=10`
- **200 OK**
- Retorna coleção HATEOAS. Com `@Relation` no DTO, o embedded fica `_embedded.ferramentas`.

### GET `/ferramentas/{id}`
- **200 OK** | **404 Not Found**

### POST `/ferramentas`
- **201 Created** + `Location: /ferramentas/{id}`
- Body (exemplo):
```json
{
  "nome": "Serra",
  "tipo": "Manual",
  "classificacao": "Uso geral",
  "tamanho": "Pequeno",
  "preco": 15.75
}
```

### PUT `/ferramentas/{id}`
- **200 OK** | **404 Not Found**

### PATCH `/ferramentas/{id}`
- **200 OK** | **404 Not Found**

### DELETE `/ferramentas/{id}`
- **204 No Content** | **404 Not Found**

---

## HATEOAS
Cada item inclui:
- `self` → `/ferramentas/{id}`
- `collection` → `/ferramentas?page=..&size=..`
- `update`, `delete`

A coleção tem `self` e, opcionalmente (se habilitado com `PagedResourcesAssembler`), links `first/prev/next/last` + bloco `page`.

---

## Rodar localmente (sem Render)

### Requisitos
- JDK 21, Maven 3.9+
- Oracle (pode ser **Oracle XE via Docker Compose**).

### Com Docker Compose (app + Oracle XE)
```bash
mvn -DskipTests clean package
docker compose up -d --build
# http://localhost:8081/ferramentas
```

### Só com Java (sem Docker)
Ajuste `application.properties` com suas credenciais Oracle; depois:
```bash
mvn -DskipTests clean package
java -jar target/ferramentas-0.0.1-SNAPSHOT.jar
# http://localhost:8081
```

---

## Deploy no Render (como foi feito)
- **Dockerfile multi-stage** compila e empacota a app no build do Render:
```dockerfile
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
COPY pom.xml .
COPY src ./src
RUN mvn -DskipTests clean package

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /build/target/*-SNAPSHOT.jar /app/app.jar
ENV PORT=8081
EXPOSE 8081
ENTRYPOINT ["sh","-c","java -jar /app/app.jar --server.port=${PORT}"]
```
- Propriedade de porta no app: `server.port=${PORT:8081}`
- Variáveis de ambiente configuradas no Render:
    - `SPRING_DATASOURCE_URL`
    - `SPRING_DATASOURCE_USERNAME`
    - `SPRING_DATASOURCE_PASSWORD`
    - `SPRING_JPA_HIBERNATE_DDL_AUTO=none`
    - `SPRING_SQL_INIT_MODE=never`

> Observação: o banco precisa aceitar conexões externas a partir da infraestrutura do Render.

---

## Troubleshooting
- **Primeira chamada demora:** serviço pode estar hibernado pelo Render.
- **500/ORA-00942 no Compose local:** a tabela não existe. Ligue o `schema.sql` na 1ª subida (`spring.sql.init.mode=always`) e depois retorne a `never`.
- **ORA-00955 em ambiente local:** já existe objeto com o mesmo nome. Use `continue-on-error=true` ou `DROP` dos objetos e suba novamente.
- **Timeout de DB no Render:** banco não acessível publicamente; use um Oracle público ou rode com Compose (app+DB) numa VPS.

---

## IDE utilizada:

IntelliJ
 
---

## Prints

<img width="1142" height="453" alt="imagem" src="https://github.com/user-attachments/assets/44145751-2434-48ee-9308-d54ea6cdb74c" />

<img width="1364" height="275" alt="image" src="https://github.com/user-attachments/assets/82153d4e-8d34-4b06-97fd-113e304ddeb6" />

<img width="941" height="605" alt="image" src="https://github.com/user-attachments/assets/094a9c35-b006-4e95-8730-3629ec50c773" />



---

## Créditos
- **Autores:** Lu Vieira Santos, Diego Furigo, Melissa Pereira
- **Projeto:** CP Spring Boot — API de Ferramentas (Oracle + HATEOAS)
