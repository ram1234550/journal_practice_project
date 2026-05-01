# Journal MVP

Minimal journal workflow MVP on Spring Boot:

- author registration and login
- article submission
- admin reviewer assignment
- reviewer decision flow
- public published articles page

## Demo accounts

When `APP_SEED_DEMO_DATA=true`, the app creates:

- `admin@journal.local` / `admin123`
- `reviewer@journal.local` / `reviewer123`
- `author@journal.local` / `author123`

## Local run

### Option 1: Docker Compose

```bash
docker compose up --build
```

Open:

- app: `http://localhost:8080`
- public articles: `http://localhost:8080/published.html`
- login: `http://localhost:8080/login.html`

### Option 2: Local PostgreSQL + Spring Boot

1. Create a PostgreSQL database named `journal_db`
2. Set environment variables:

```bash
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/journal_db
SPRING_DATASOURCE_USERNAME=postgres
SPRING_DATASOURCE_PASSWORD=postgres
JWT_SECRET=change-this-secret
APP_SEED_DEMO_DATA=true
```

3. Run:

```bash
./mvnw spring-boot:run
```

On Windows PowerShell:

```powershell
.\mvnw.cmd spring-boot:run
```

## What initializes automatically

- `schema.sql` creates the tables
- `DemoDataSeeder` creates demo users and sample articles
- static frontend is served from `src/main/resources/static`

## Test

```bash
./mvnw test
```

## Recommended deployment

The most predictable deployment path is a Linux VPS with Docker Compose.

### 1. Prepare the server

- install Docker
- install Docker Compose plugin
- open port `8080` or put Nginx in front

### 2. Copy the project

```bash
git clone <your-repo-url>
cd journal_practice_project
```

### 3. Set production values

Edit `docker-compose.yml` or use an `.env` file:

- strong `JWT_SECRET`
- real PostgreSQL password
- optional `APP_SEED_DEMO_DATA=false` after first boot

### 4. Start it

```bash
docker compose up -d --build
```

### 5. Optional reverse proxy with Nginx

Proxy your domain to `http://127.0.0.1:8080`.

That gives you:

- Spring Boot app
- PostgreSQL
- persistent DB volume
- one-command restart/update

## First demo flow

1. Open `/login.html`
2. Login as author and submit an article
3. Login as admin and assign a reviewer
4. Login as reviewer and publish or request revision
5. Open `/published.html` to see public results
