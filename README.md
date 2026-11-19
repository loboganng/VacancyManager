# Vacancy Manager API

![CI](https://github.com/loboganng/VacancyManager/actions/workflows/ci.yml/badge.svg)

REST API for a job board. Companies post vacancies and see who applied; candidates search vacancies and apply.
Each user type logs in separately and gets its own JWT.

**Stack:** Java 17, Spring Boot 3.5, Spring Security, Spring Data JPA, PostgreSQL, java-jwt, springdoc-openapi, JUnit 5, Mockito, MockMvc, H2 (tests), Docker, GitHub Actions.

> Live demo: _not deployed yet._

## Endpoints

| Method | Path | Auth | What it does |
|---|---|---|---|
| POST | `/company/` | public | Sign up a company |
| POST | `/company/auth` | public | Company login, returns a company JWT (2 h) |
| POST | `/company/job` | company | Create a vacancy |
| GET | `/company/job` | company | List the logged-in company's vacancies |
| GET | `/company/job/{id}/applications` | company | Candidates who applied to one of the company's vacancies |
| POST | `/candidate/` | public | Sign up a candidate |
| POST | `/candidate/auth` | public | Candidate login, returns a candidate JWT (10 min) |
| GET | `/candidate/` | candidate | Logged-in candidate's profile |
| GET | `/candidate/job?filter=java` | candidate | Search vacancies by description (case-insensitive) |
| POST | `/candidate/job/apply` | candidate | Apply to a vacancy: `{"jobId": "<uuid>"}` |

Full, interactive docs: **Swagger UI** at `http://localhost:8080/swagger-ui/index.html` (OpenAPI JSON at `/v3/api-docs`).
Click **Authorize** and paste the token in `jwt_company` or `jwt_candidate`.

### Errors

| Status | When |
|---|---|
| 400 | Invalid body; the response lists each field and its message |
| 401 | Missing, expired or wrong-type token; wrong username/password |
| 403 | Valid token without the required role |
| 404 | Job or user not found (a job owned by another company also returns 404) |
| 409 | Username/email already taken; candidate already applied to that job |

## Running locally

Requirements: Docker. Java 17+ only if you want to run outside Docker.

```bash
git clone https://github.com/loboganng/VacancyManager.git
cd VacancyManager
cp .env.example .env          # then edit the two JWT secrets
docker compose --profile app up --build
```

The API is on `http://localhost:8080`. Without `--profile app`, Compose starts only PostgreSQL, so you can run the API from your IDE or with:

```bash
export JWT_SECRET=dev-company-secret JWT_SECRET_CANDIDATE=dev-candidate-secret
./mvnw spring-boot:run
```

### Configuration

| Variable | Default | Notes |
|---|---|---|
| `JWT_SECRET` | none (required) | Signs company tokens |
| `JWT_SECRET_CANDIDATE` | none (required) | Signs candidate tokens |
| `DATABASE_URL` | `jdbc:postgresql://localhost:5432/gestao_vagas` | JDBC URL |
| `DATABASE_USERNAME` / `DATABASE_PASSWORD` | `admin` / `admin` | Local dev values only |
| `PORT` | `8080` | HTTP port |

Secrets can also go in a local `secrets.properties` file (ignored by git).

### Quick try with curl

```bash
curl -X POST localhost:8080/company/ -H 'Content-Type: application/json' \
  -d '{"username":"acme","email":"hr@acme.test","password":"password123","name":"Acme"}'

TOKEN=$(curl -s -X POST localhost:8080/company/auth -H 'Content-Type: application/json' \
  -d '{"username":"acme","password":"password123"}' | sed -E 's/.*"access_token":"([^"]+)".*/\1/')

curl -X POST localhost:8080/company/job -H "Authorization: Bearer $TOKEN" -H 'Content-Type: application/json' \
  -d '{"description":"Junior Java developer","benefits":"Remote","level":"JUNIOR"}'
```

## Tests

```bash
./mvnw test
```

No database or environment variables needed: tests run with the `test` profile (in-memory H2 and fake secrets).

- **Unit tests** (JUnit 5 + Mockito) for the use cases: duplicated company, invalid login, token content, applying to a missing job, duplicated application (including the race caught by the unique constraint), and a company trying to read another company's applications.
- **Integration tests** (MockMvc + H2) that boot the whole app: the full flow (company posts, candidate searches and applies, company lists applicants), token separation between user types, validation errors, password never returned in responses, and sign-up ignoring a client-supplied `id` (so it cannot overwrite another account).

GitHub Actions runs `./mvnw test` on every push and pull request.

## Design decisions

- **Two JWTs, two secrets.** Companies and candidates sign tokens with different secrets and each security filter only validates its own path prefix (`/company/**` or `/candidate/**`). A leaked candidate secret cannot forge company tokens, and a candidate token is rejected on company routes before any role check.
- **Role check on top of the token.** Controllers also use `@PreAuthorize("hasRole(...)")`, so the rule is visible where the endpoint is declared.
- **Use cases.** Each business action is one class (`CreateJobUseCase`, `ApplyJobCandidateUseCase`, ...). Controllers only translate HTTP to use-case calls; errors become HTTP statuses in one `@ControllerAdvice`.
- **One application per candidate and job**, enforced twice: a check in the use case (clear 409 message) and a unique constraint in the database (safe under concurrent requests).
- **404 for another company's job**, so the API does not reveal which job ids exist.
- **Response DTOs** for jobs and applications, and the password field is write-only, so password hashes never leave the API.

## Origin

Started from the Rocketseat Java course project. Beyond the course: the vacancy search and application flow, the applicants list, centralized error handling, the test suite, Swagger with both auth schemes, Docker image and CI.
