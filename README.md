# The Cheat School & Emiratiyo Investments Backend

A unified Spring Boot monolith backend serving two distinct platforms: The Cheat School (TCS) and Emiratiyo Investments (EM). Follows a layered architecture to manage multiple services within a single codebase.

---

## Architecture Overview

- **Controllers** — Handle incoming HTTP requests for both TCS and EM
- **Services** — Domain-specific logic (e.g. `EMContactService` vs `TCSContactService`)
- **Models/Repositories** — Dedicated data entities and JPA repositories per service
- **Shared Infrastructure** — Spring `@Async` thread pool (email), Bucket4j (rate limiting), Resilience4j (circuit breaking), Upstash Redis (caching)

---

> **Migration Notice**
> Previously maintained at [github.com/thecheatschool/the-cheat-school-server](https://github.com/thecheatschool/the-cheat-school-server).
> Now maintained at [github.com/EmiratiyoInvestments/emiratiyo-investments-api](https://github.com/EmiratiyoInvestments/emiratiyo-investments-api).

---

## Live API

```
https://thecheatschool-api.fly.dev
```

Deployed on **Fly.io** · Region: `bom` (Mumbai)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 17 |
| Framework | Spring Boot 3.5.7 |
| Database | PostgreSQL (Neon serverless) |
| ORM | Spring Data JPA / Hibernate |
| Email | Resend API (via `RestTemplate`) |
| AI | Google Gemini 2.5 Flash |
| Async | Spring `@Async` + `ThreadPoolTaskExecutor` |
| Cache | Upstash Redis (Spring Cache) |
| Rate Limiting | In-memory sliding window interceptor |
| Circuit Breaker | Resilience4j |
| Streaming | Spring SSE (`SseEmitter`) |
| Monitoring | Spring Boot Admin (embedded) |
| Container | Docker |
| Deployment | Fly.io |

---

## Project Structure

```
thecheatschoolserver/
├── src/main/java/com/thecheatschool/thecheatschool/server/
│   ├── config/                  # CORS, Redis, Security, Async, RateLimiter configs
│   ├── controller/              # REST controllers for TCS, EM, and shared endpoints
│   ├── exception/               # Global exception handler
│   ├── model/
│   │   ├── tcs/                 # TCS request/entity models
│   │   └── em/                  # EM request/entity models (incl. Emira AI)
│   ├── repository/              # Spring Data JPA repositories
│   ├── service/
│   │   ├── tcs/                 # TCS business logic + email service
│   │   └── em/                  # EM business logic, email service, Emira AI service
│   └── util/                    # Input sanitizer, rate-limiting interceptor
├── src/main/resources/
│   └── application.properties   # All environment configuration
├── Dockerfile
├── fly.toml                     # Fly.io deployment config
└── pom.xml
```

---

## Getting Started

**Prerequisites:** Java 17+, Maven 3.6+, PostgreSQL (or Neon.tech), Upstash Redis

```bash
git clone https://github.com/EmiratiyoInvestments/emiratiyo-investments-api.git
mvn clean install
mvn test
mvn spring-boot:run
```

---

## Configuration & Secrets

**TCS:** `resend.api.key`, `contact.recipient.email`

**EM:** `em.resend.api.key`, `em.contact.recipient.email`, `emira.gemini.primary-key`, `emira.internal.secret`

See [docs/tcs/ENV_SETUP.md](docs/tcs/ENV_SETUP.md) and [docs/emiratiyo/ENV_SETUP.md](docs/emiratiyo/ENV_SETUP.md) for full details.

---

## Deployment

**Platform:** Fly.io — `https://thecheatschool-api.fly.dev`

```bash
flyctl auth login --email thecheatschoolcode@gmail.com
flyctl secrets list
flyctl deploy
flyctl logs
```

See [docs/tcs/DEPLOYMENT.md](docs/tcs/DEPLOYMENT.md) and [docs/emiratiyo/DEPLOYMENT.md](docs/emiratiyo/DEPLOYMENT.md) for full details.

---

## Monitoring

This server embeds **Spring Boot Admin** — a full operational dashboard running inside the same process.

| Environment | URL |
|---|---|
| Production | [https://thecheatschool-api.fly.dev/](https://thecheatschool-api.fly.dev/) |

Navigate to the root URL and log in with the credentials configured in `application.properties`:

```
Username: admin
Password: tcs-monitor-2025
```

The dashboard exposes: health indicators, JVM metrics, live loggers, environment variables, bean graph, thread dump, and Redis cache stats — all without a separate admin process.

---

## Documentation

### The Cheat School (TCS)

| Doc | Description |
|---|---|
| [ARCHITECTURE.md](docs/tcs/ARCHITECTURE.md) | System design, layers, auth, middleware |
| [API_REFERENCE.md](docs/tcs/API_REFERENCE.md) | All TCS endpoints |
| [ENV_SETUP.md](docs/tcs/ENV_SETUP.md) | Environment variables, local setup |
| [DEPLOYMENT.md](docs/tcs/DEPLOYMENT.md) | Fly.io deployment, secrets, rollback |
| [CONTRIBUTING.md](docs/tcs/CONTRIBUTING.md) | Branching, PRs, code style |

### Emiratiyo Investments (EM)

| Doc | Description |
|---|---|
| [ARCHITECTURE.md](docs/emiratiyo/ARCHITECTURE.md) | System design, layers, auth, Emira AI |
| [API_REFERENCE.md](docs/emiratiyo/API_REFERENCE.md) | All EM endpoints |
| [ENV_SETUP.md](docs/emiratiyo/ENV_SETUP.md) | Environment variables, local setup |
| [DEPLOYMENT.md](docs/emiratiyo/DEPLOYMENT.md) | Fly.io deployment, secrets, rollback |
| [CONTRIBUTING.md](docs/emiratiyo/CONTRIBUTING.md) | Branching, PRs, code style |

---

## License

© [Emiratiyo](https://emiratiyo.com). All Rights Reserved.
