# lowcode

Backend for a low-code platform, built with Spring Boot. It currently contains one
bounded context: **Access Control** (RBAC) — Users, Roles, Permissions and
authorization decisions, exposed over both REST and GraphQL.

See [GLOSSARY.md](GLOSSARY.md) for the ubiquitous language and
[docs/adr/](docs/adr) for architecture decision records.

## Prerequisites

| Tool       | Version            | Required for                     | Notes                                                                 |
| ---------- | ------------------ | -------------------------------- | --------------------------------------------------------------------- |
| JDK        | **25**             | build & run                      | `java.version=25` in `pom.xml` (Spring Boot 4 requires Java 17+)      |
| Maven      | **3.9+**           | build & run                      | No Maven wrapper is committed; `mvn` must be on the `PATH`            |
| Docker     | any recent version | **tests only**                   | Integration tests start PostgreSQL via Testcontainers                 |
| PostgreSQL | **17**             | running the app                  | Tests do *not* need a local database; they use a container            |

### Docker images

| Image                    | Used by                                                              |
| ------------------------ | -------------------------------------------------------------------- |
| `postgres:17.11-bookworm` | Testcontainers integration tests (`src/test/java/.../TestPostgreSql.java`) |

## Stack

- **Spring Boot 4.1.1** — Web MVC, Data JPA, GraphQL, Security, RestClient, Liquibase
- **springdoc-openapi 3.1.0** — Swagger UI
- **PostgreSQL JDBC driver** (runtime)
- **Lombok** — annotation processing is configured in the `maven-compiler-plugin`
- **Tests** — JUnit Jupiter, Spring Boot test starters, Testcontainers (`junit-jupiter`, `postgresql`)

## Configuration

All settings live in `src/main/resources/application.properties` and can be
overridden with environment variables:

| Variable                        | Default                                  | Purpose                              |
| ------------------------------- | ---------------------------------------- | ------------------------------------ |
| `DATABASE_URL`                  | `jdbc:postgresql://localhost:5432/lowcode` | JDBC URL                             |
| `DATABASE_USERNAME`             | `lowcode`                                | Database user                        |
| `DATABASE_PASSWORD`             | `lowcode`                                | Database password                    |
| `ACCESS_CONTROL_ADMIN_USERNAME` | `admin`                                  | Bootstrap administrator username     |
| `ACCESS_CONTROL_ADMIN_PASSWORD` | *(empty)*                                | Bootstrap administrator password     |

- The schema is owned by **Liquibase** (`db/changelog/db.changelog-master.yaml`);
  Hibernate runs with `ddl-auto=validate` and never alters the schema.
- On first startup a **Bootstrap Administrator** is created (role `super-admin`)
  when `lowcode.access-control.bootstrap.enabled=true`.

## Local database

To run the app locally, start a PostgreSQL matching the defaults above:

```bash
docker run -d --name lowcode-db \
  -e POSTGRES_DB=lowcode \
  -e POSTGRES_USER=lowcode \
  -e POSTGRES_PASSWORD=lowcode \
  -p 5432:5432 \
  postgres:17.11-bookworm
```

## Build & test

```bash
mvn clean verify        # full build; Docker must be running (Testcontainers)
mvn -DskipTests package # build the jar without tests
```

The artifact is `target/lowcode-0.0.1-SNAPSHOT.jar`.

## Run

```bash
mvn spring-boot:run
# or
java -jar target/lowcode-0.0.1-SNAPSHOT.jar
```

The app listens on port **8080** by default.

## Endpoints

| Endpoint                                          | Description                                  |
| ------------------------------------------------- | -------------------------------------------- |
| `/api/access/admin/users`, `/roles`, `/permissions` | Access administration REST API               |
| `/api/access/me`, `/api/access/decisions/{permissionCode}` | Identity & authorization REST API   |
| `/graphql`                                        | GraphQL API (schema: `graphql/access-control.graphqls`) |
| `/swagger-ui.html`                                | Swagger UI (permitAll)                       |
| `/login`                                          | Form login                                   |

Authentication uses form login or HTTP Basic. CSRF protection is enforced for
session (cookie) credentials but skipped for requests carrying an
`Authorization` header; unauthenticated `/api/**` and `/graphql` requests get a
plain `401`.
