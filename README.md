# The Cheat School & Emiratiyo Investments Backend

A unified Spring Boot monolith backend serving two distinct platforms: **The Cheat School (TCS)** and **Emiratiyo Investments (EM)**. This project follows a layered architecture to manage multiple services within a single codebase.

---

## 🏗️ Architecture Overview

The codebase is structured as a **Monolith Layered Architecture**, where common infrastructure (CORS, Rate Limiting, RabbitMQ) is shared, while business logic is separated by service:

- **Controllers**: Handle incoming HTTP requests for both TCS and EM.
- **Services**: Contain domain-specific logic (e.g., `EMContactService` vs `TCSContactService`).
- **Models/Repositories**: Dedicated data entities and JPA repositories for each service.
- **Shared Infrastructure**:
  - `RabbitMQ`: Background email processing.
  - `Bucket4j`: Rate limiting for API protection.
  - `Resilience4j`: Circuit breaking for external AI integrations (Emira).

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Maven 3.6+
- PostgreSQL (or Neon.tech connection)
- RabbitMQ (Optional, can be disabled in properties)

### Local Development
1. **Clone the repository**
2. **Install dependencies:**
   ```bash
   mvn clean install
   ```
3. **Run tests:**
   ```bash
   mvn test
   ```
4. **Run application:**
   ```bash
   mvn spring-boot:run
   ```

---

## 🔑 Configuration & Secrets

The application uses `application.properties` to distinguish between TCS and EM configurations.

### The Cheat School (TCS) Keys
- `resend.api.key`: Email API key for TCS.
- `contact.recipient.email`: Destination for TCS contact forms.

### Emiratiyo Investments (EM) Keys
- `em.resend.api.key`: Email API key for EM.
- `em.contact.recipient.email`: Destination for EM contact forms.
- `emira.gemini.primary-key`: AI Analyst primary API key.
- `emira.internal.secret`: Security key for internal EM tools.

---

## ☁️ Deployment (Fly.io)

The backend is deployed on Fly.io under `thecheatschool-api.fly.dev`.

### Useful Commands
1. **Login:**
   ```bash
   flyctl auth login --email thecheatschoolcode@gmail.com
   ```
2. **View Secrets:**
   ```bash
   flyctl secrets list
   ```
3. **Deploy Changes:**
   ```bash
   flyctl deploy
   ```
4. **View Logs:**
   ```bash
   flyctl logs
   ```

---

## 🛠️ Tech Stack
- **Framework**: Spring Boot 3.5.7
- **Database**: PostgreSQL (Neon.tech)
- **Email**: Resend API
- **AI**: Google Gemini 2.5 Flash (via WebFlux)
- **Messaging**: RabbitMQ (CloudAMQP)
- **Hosting**: Fly.io
