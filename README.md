# Bookt

A concurrency-safe resource-booking backend built with Spring Boot and PostgreSQL, designed to demonstrate correct handling of simultaneous booking requests without application-level locking.

## The Problem

Most booking-app tutorials handle double-booking with `synchronized` blocks, optimistic locking retries, or a `SELECT ... FOR UPDATE` — approaches that work under light load but add complexity and don't scale cleanly across multiple app instances.

Bookt takes a different approach: it pushes the conflict check down into the database itself using a **PostgreSQL GiST exclusion constraint** on the booking time range. Two overlapping bookings for the same resource are structurally impossible to commit — the database rejects the second write outright, at the storage layer, regardless of how many application instances or threads are racing to book the same slot.

## Key Technical Feature

The centerpiece of this project is a concurrency stress test: hundreds of threads fire booking requests at the *same* resource and time slot simultaneously. The test asserts that **exactly one** request succeeds and every other request receives a clean `409 Conflict` — with no locks, no retries, and no race condition handling in application code.

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 3.5.x |
| Database | PostgreSQL 16 |
| Migrations | Flyway (`ddl-auto: validate`) |
| Query Builder | jOOQ (type-safe SQL for queries JPA doesn't express well) |
| Testing | JUnit 5, Testcontainers (no H2 — integration tests run against real Postgres) |
| Build | Maven |
| Containerization | Docker |
| CI | GitHub Actions |
| Production DB | Neon (managed Postgres) |
| Hosting | Render |
| Container Orchestration | Kubernetes (GKE Autopilot) |
| Container Registry | Google Artifact Registry |

## Architecture

- **Entities:** `Tenant`, `Resource`, `Booking`, modeled with proper JPA relationships (`@ManyToOne` associations rather than raw foreign-key UUIDs).
- **Booking lifecycle:** a hold-then-confirm-then-expire flow. A booking is placed on hold, confirmed within a window, and automatically expired by a scheduled job (`HoldExpirationJob`) if not confirmed in time.
- **Conflict prevention:** an exclusion constraint on the `time_range` column (a Postgres `GENERATED ALWAYS` range column, intentionally excluded from the JPA entity) guarantees no two bookings for the same resource can overlap, enforced at the database level.
- **Availability pre-check:** a jOOQ query checks for overlapping bookings using Postgres's native `&&` range operator, the same operator the exclusion constraint itself relies on, giving callers a clean rejection before attempting a write. JPA handles this poorly since it isn't built for range-overlap queries, jOOQ is used here specifically, not throughout the codebase, since standard CRUD stays on JPA.
- **Idempotency:** write endpoints support idempotency keys so retried requests (e.g. from flaky clients) don't create duplicate bookings.
- **Error handling:** centralized via `GlobalExceptionHandler`, translating domain exceptions (`BookingConflictException`, `ResourceNotFoundException`) into clean HTTP responses.

## Getting Started

### Prerequisites

- Java 21
- Docker (for local Postgres)
- Maven

### Run locally

```bash
# Start a local Postgres instance
docker compose up -d

# Run the application
./mvnw spring-boot:run
```

The app will apply Flyway migrations automatically on startup and connect to the containerized Postgres instance defined in `docker-compose.yml`.

### Run the test suite

```bash
./mvnw test
```

Integration tests spin up a real PostgreSQL container via Testcontainers — no embedded/in-memory database is used, so tests exercise the actual exclusion constraint behavior.

## Configuration

| Profile | Purpose |
|---|---|
| `default` (local) | Points at the local Dockerized Postgres instance |
| `prod` | Points at the Neon managed database via `DB_URL`, `DB_USERNAME`, and `DB_PASSWORD` environment variables, with `sslmode=require` |

The database connection details are externalized to environment variables rather than hardcoded in `application-prod.yml`, since a Neon connection string embeds the project's compute endpoint, cloud provider, and region in the hostname itself (e.g. `ep-xxx-xxx.us-east-2.aws.neon.tech`). Keeping `DB_URL` configurable — alongside the existing credential env vars — means the app can point at a different Neon branch, region, or project without any code or config changes.

Activate the production profile with:

```
SPRING_PROFILES_ACTIVE=prod
```

## Deployment

Bookt runs in production on Render, connected to a Neon-managed Postgres instance.

The app is also deployable to Google Kubernetes Engine, using GKE Autopilot so node management is handled by GCP rather than manually provisioned. The Docker image is built and pushed to Google Artifact Registry, then deployed via the manifests in `k8s/`:

- `k8s/deployment.yaml` — defines the pod spec, resource requests/limits, and environment configuration (database credentials pulled from a Kubernetes Secret rather than hardcoded)
- `k8s/service.yaml` — exposes the deployment through a GCP-provisioned LoadBalancer with a stable external IP

```bash
# Build and push the image
docker build -t europe-west2-docker.pkg.dev/bookt-deployment/bookt-repo/bookt:latest .
docker push europe-west2-docker.pkg.dev/bookt-deployment/bookt-repo/bookt:latest

# Deploy
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

## Project Status

- ✅ Core domain model and booking lifecycle
- ✅ GiST exclusion constraint for conflict-free concurrent booking
- ✅ jOOQ availability pre-check using native Postgres range overlap
- ✅ Idempotency key support
- ✅ CI pipeline (GitHub Actions)
- ✅ Dockerized deployment to Render (live, connected to Neon)
- ✅ Kubernetes manifests for GKE deployment (Autopilot, Artifact Registry)
- ⬜ Concurrency stress test suite (centerpiece test)
- ⬜ Authentication (JWT)
- ⬜ Frontend + embeddable booking widget
- ⬜ Multi-tenant SaaS features (tenant registration, public booking links)

## Roadmap

This project is being built incrementally as a portfolio piece. Planned next steps:

1. Concurrency stress test proving the exclusion-constraint guarantee under load
2. JWT-based authentication
3. Frontend client and embeddable JS widget for cross-origin booking
4. Multi-tenant SaaS expansion (tenant onboarding, resource management UI, public booking links)

## Why This Project

Bookt was built to demonstrate backend engineering judgment beyond CRUD: choosing the database as the source of truth for a correctness guarantee, rather than reaching for locks or retries in application code, and verifying that guarantee under real concurrent load rather than assuming it.

## License

MIT
