# 📏 QuantityMeasurementApp (UC21 - Microservices)

> A Java-based Spring Boot microservices platform for quantity measurement, authentication, and API routing. Built using Test-Driven Development (TDD), this project evolves the original quantity-measurement domain into a production-style distributed architecture with service discovery, gateway-based security, and centralized monitoring.

### 📖 Overview

- Modular Spring Boot system split into independent services for measurement logic, user/authentication, service discovery, gateway routing, and admin monitoring.
- Maintains the original UC1-UC19 measurement and authentication capabilities while introducing UC21 microservices architecture and deployment workflows.
- Designed for local development with Docker Compose and production deployment with environment-driven configuration.

### ✅ Implemented Features

- 🧩 **UC1-UC18 - Core Domain and Security Evolution:**
  - Quantity measurement capabilities for length, weight, volume, and temperature.
  - Generic quantity model with conversion and arithmetic support (capability-aware for temperature).
  - Spring Boot REST APIs, JPA persistence, validation, global exception handling, and actuator endpoints.
  - JWT authentication, Google/GitHub OAuth2 login, OTP-based password reset flow, and role-aware access control.

8- 🧩 **UC21 - Microservices Architecture:**

- Splits the backend into five services:
  - `measurement-service` - quantity operations and measurement history.
  - `user-service` - authentication, user management, OAuth2, OTP, and email notifications.
  - `api-gateway` - single public entrypoint, JWT validation, and request routing.
  - `eureka-server` - service discovery/registry.
  - `admin-server` - Spring Boot Admin dashboard for service health/metrics.
- Adds service discovery using Eureka and load-balanced gateway routing with `lb://` URIs.
- Centralizes external API exposure at gateway port `8080`; internal services remain independently deployable.
- Supports local single-command startup using Docker Compose with health-ordered boot sequence.
- Supports production override via `docker-compose.prod.yml` using RDS and restricted port exposure.

### 🧰 Tech Stack

- **Java 17+** - core language
- **Maven** - build and dependency management

#### 🚀 Backend Framework

- **Spring Boot 3.2.2** - base framework
- **Spring Web** - REST APIs
- **Spring Data JPA** - persistence layer
- **Spring Security + OAuth2 Client** - JWT and social auth flows
- **Spring Cloud Gateway** - API gateway and routing
- **Spring Cloud Netflix Eureka** - service discovery
- **Spring Boot Admin** - service monitoring UI
- **Spring Boot Actuator** - health/info/metrics

#### 🗄️ Database

- **H2** - dev/test persistence
- **MySQL** - production persistence

#### 📄 API Documentation

- **Swagger / OpenAPI (springdoc-openapi)** - interactive API docs for service APIs

#### ⚙️ Utilities

- **Lombok** - boilerplate reduction
- **SLF4J + Logback** - logging
- **HikariCP** - connection pooling

#### 🧪 Testing

- **Spring Boot Test (JUnit 5, Mockito, MockMvc)**
- **Spring Security Test**

### 🧱 Microservices Topology

| Service               | Port | Responsibility                                         |
| --------------------- | ---: | ------------------------------------------------------ |
| `api-gateway`         | 8080 | Public entrypoint, routing, centralized JWT validation |
| `measurement-service` | 8081 | Quantity compare/convert/arithmetic/history APIs       |
| `user-service`        | 8082 | Auth APIs, OTP, OAuth2, user resolution                |
| `admin-server`        | 8085 | Spring Boot Admin dashboard                            |
| `eureka-server`       | 8761 | Service registry/discovery                             |
| `mysql` (docker)      | 3306 | `user_db` and `measurement_db`                         |

### 🔀 Gateway Routes

Configured in `api-gateway/src/main/resources/application.properties`:

- `/api/v1/quantities/**` -> `measurement-service`
- `/api/v1/auth/**` -> `user-service`
- `/api/v1/users/**` -> `user-service`
- `/oauth2/**` and `/login/oauth2/**` -> `user-service`

### ▶️ Build / Run

#### Option A - Run with Docker Compose (Recommended)

1. Copy env template:

```bash
cp .env.example .env
```

2. Fill required values in `.env`:

- `DB_USERNAME`
- `DB_PASSWORD`
- `JWT_SECRET`
- `JWT_EXPIRATION_MS`
- `MAIL_USERNAME`
- `MAIL_PASSWORD`
- `GOOGLE_CLIENT_ID`
- `GOOGLE_CLIENT_SECRET`
- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`
- `CORS_ALLOWED_ORIGINS`
- `OAUTH2_REDIRECT_URI`

3. Start all services:

```bash
docker-compose up --build
```

4. Stop all services:

```bash
docker-compose down
```

#### Option B - Production Compose Override

Use RDS and expose only gateway publicly:

```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d --build
```

#### Option C - Run Services Individually (Maven)

Run each module in separate terminals:

```bash
cd eureka-server && mvn spring-boot:run
cd user-service && mvn spring-boot:run
cd measurement-service && mvn spring-boot:run
cd api-gateway && mvn spring-boot:run
cd admin-server && mvn spring-boot:run
```

### 🌐 Access URLs

- API Gateway: `http://localhost:8080`
- Eureka Dashboard: `http://localhost:8761`
- Admin Server UI: `http://localhost:8085`

#### Auth APIs (via gateway)

Base: `http://localhost:8080/api/v1/auth`

| Method | Endpoint                  | Auth Required     | Purpose                         |
| ------ | ------------------------- | ----------------- | ------------------------------- |
| POST   | `/register`               | No                | Register and receive JWT        |
| POST   | `/login`                  | No                | Login and receive JWT           |
| GET    | `/me`                     | Yes (JWT)         | Fetch current user profile      |
| POST   | `/otp/send`               | No                | Send OTP email                  |
| POST   | `/otp/verify`             | No                | Verify OTP                      |
| PUT    | `/forgotPassword/{email}` | No (OTP verified) | Reset password via OTP flow     |
| PUT    | `/resetPassword/{email}`  | Yes (JWT)         | Change password while logged in |

#### Quantity APIs (via gateway)

Base: `http://localhost:8080/api/v1/quantities`

| Method | Endpoint                          |
| ------ | --------------------------------- |
| POST   | `/compare`                        |
| POST   | `/convert`                        |
| POST   | `/add`                            |
| POST   | `/subtract`                       |
| POST   | `/divide`                         |
| GET    | `/history/operation/{operation}`  |
| GET    | `/history/type/{measurementType}` |
| GET    | `/history/errored`                |
| GET    | `/count/{operation}`              |

### ⚙️ Configuration

- `docker-compose.yml`:
  - Local development stack with MySQL + all five services.
  - Uses healthchecks and dependency ordering for stable startup.
- `docker-compose.prod.yml`:
  - Disables local MySQL.
  - Uses `RDS_ENDPOINT` for `user-service` and `measurement-service`.
  - Exposes only gateway port `8080` publicly.
- `init.sql`:
  - Creates `user_db` and `measurement_db` if absent.
  - Grants privileges for the configured app database user.

### 📂 Project Structure

```text
quantity-measurement-app/
├── admin-server/
├── api-gateway/
├── eureka-server/
├── measurement-service/
├── user-service/
├── docker-compose.yml
├── docker-compose.prod.yml
├── init.sql
├── .env.example
├── .gitignore
└── README.md
```

### ⚙️ Development Approach

> This project follows incremental Test-Driven Development (TDD):

- Write tests first for each use case and service behavior.
- Implement minimal changes to satisfy tests.
- Refactor continuously while preserving behavior.
- Scale architecture from monolith to microservices without losing domain correctness.

### 📄 License

> This project is licensed under the MIT License.

### 👨‍💻 Author

**Abhishek Puri Goswami**

---

<div align="center">
✨ Incrementally developed using TDD, now evolved into UC21 microservices architecture.
</div>
