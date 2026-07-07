# Emiratiyo Investments API

A Spring Boot monolith backend serving the Emiratiyo Investments (EM) platform. This project is architected with a focus on resilience, non-blocking I/O, and strict data separation.

---

## Architecture Overview

- **Package-by-Layer Architecture** — Clean separation of concerns across `controller`, `service`, `repository`, `entity`, and `dto`.
- **Entities vs. DTOs** — Strict separation between database persistence models (`@Entity`) and API data transfer objects (`record`).
- **Reactive Resilience** — Implementation of Circuit Breaker and Retry patterns for external AI and Email services.
- **API Versioning** — All endpoints follow the `/api/v1/` standard for long-term maintainability.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.4.5 |
| Database | PostgreSQL (Neon serverless) |
| ORM | Spring Data JPA / Hibernate |
| HTTP Client | Spring WebClient (Reactive) |
| AI | Google Gemini 2.5 Flash |
| Resilience | Resilience4j (Circuit Breaker) |
| Cache | Upstash Redis (Spring Cache) |
| Rate Limiting | Bucket4j Interceptor |
| Monitoring | Spring Boot Admin (embedded) |
| Deployment | Fly.io |

---

## Project Structure

```
emiratiyo-api/
├── src/main/java/com/emiratiyo/api/
│   ├── config/                  # WebClient, Redis, Security, Async, RateLimiter configs
│   ├── controller/              # Versioned REST controllers (/api/v1/...)
│   ├── dto/                     # Immutable Java Records for API Requests/Responses
│   ├── entity/                  # JPA Database Entities
│   ├── exception/               # Custom business exceptions & Global Handler
│   ├── repository/              # Spring Data JPA repositories
│   ├── service/                 # Business logic & AI/Email integration services
│   └── util/                    # Shared utility classes and interceptors
├── src/main/resources/
│   └── application.properties   # Environment configuration & Resilience4j settings
├── Dockerfile
├── fly.toml                     # Fly.io deployment config
└── pom.xml
```

---

## Getting Started

**Prerequisites:** Java 21, Maven 3.9+, PostgreSQL, Upstash Redis

```bash
git clone https://github.com/EmiratiyoInvestments/emiratiyo-investments-api.git
mvn clean install
mvn spring-boot:run
```

The application starts on [http://localhost:8080](http://localhost:8080) by default.

---

## Configuration & Secrets

The following keys are required in `application.properties`:
- `em.resend.api.key`: API key for Resend email dispatch.
- `emira.gemini.primary-key`: Google Gemini AI key.
- `emira.internal.secret`: Shared secret for internal analyst endpoints.
- `spring.data.redis.*`: Upstash Redis credentials.

## Documentation

- [API_REFERENCE.md](docs/API_REFERENCE.md) — Full endpoint documentation and request/response schemas.

---

## License

© [Emiratiyo](https://emiratiyo.com). All Rights Reserved.