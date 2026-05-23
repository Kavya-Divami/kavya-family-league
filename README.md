# Family League - Prediction Platform

A Spring Boot backend for a family/friends sports prediction game where users predict match and league outcomes to earn points.

---

## Prerequisites

Before running, make sure you have installed:

| Tool | Version | Install |
|---|---|---|
| Java | 17 | `brew install openjdk@17` |
| Maven | 3.8+ | `brew install maven` |
| PostgreSQL | Any | `brew install postgresql` |

---

## Setup Steps

### 1. Clone the repository
```bash
git clone https://github.com/Kavya-Divami/kavya-family-league.git
cd kavya-family-league/family-league
```

### 2. Start PostgreSQL
```bash
brew services start postgresql
```

### 3. Create the database
```bash
psql -U postgres -c "CREATE DATABASE family_league;"
```

### 4. Configure environment variables
```bash
cp .env.example .env
```
Open `.env` and fill in your values:
```
DB_URL=jdbc:postgresql://localhost:5432/family_league
DB_USERNAME=postgres
DB_PASSWORD=your_postgres_password
JWT_SECRET=your_secret_key_minimum_32_characters_long
JWT_EXPIRATION_MS=86400000
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your_email@gmail.com
MAIL_PASSWORD=your_email_app_password
```

### 5. Set Java 17 as default (one time only)
```bash
echo 'export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.19/libexec/openjdk.jdk/Contents/Home' >> ~/.zshrc
echo 'export PATH="$JAVA_HOME/bin:$PATH"' >> ~/.zshrc
source ~/.zshrc
```

### 6. Run the application
```bash
mvn spring-boot:run
```

The app starts at **http://localhost:8080**

---

## API Documentation (Swagger)

Once running, open:
```
http://localhost:8080/swagger-ui/index.html
```

### How to use Swagger:
1. Call `POST /api/v1/auth/register` to create your account
2. Copy the `token` from the response
3. Click **Authorize** (🔒) at the top → paste `Bearer <token>` → click Authorize
4. All endpoints are now accessible

---

## API Overview

| Group | Base URL | Access |
|---|---|---|
| Auth | `/api/v1/auth` | Public |
| Users | `/api/v1/users` | Admin |
| Leagues | `/api/v1/leagues` | Admin write, Auth read |
| Seasons | `/api/v1/seasons` | Admin write, Auth read |
| Teams | `/api/v1/teams` | Admin write, Auth read |
| Players | `/api/v1/teams/{id}/players` | Admin write, Auth read |
| Matches | `/api/v1/matches` | Admin write, Auth read |
| Match Results | `/api/v1/matches/{id}/result` | Admin only |
| Match Predictions | `/api/v1/matches/{id}/predictions` | Authenticated |
| League Predictions | `/api/v1/seasons/{id}/predictions/league` | Authenticated |
| Leaderboard | `/api/v1/seasons/{id}/leaderboard` | Authenticated |

---

## Tech Stack

- **Java 17** + **Spring Boot 3.2.5**
- **PostgreSQL** — database
- **Flyway** — automatic DB migrations on startup
- **Spring Security + JWT** — authentication
- **Swagger/OpenAPI** — API documentation
