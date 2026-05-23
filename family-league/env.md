# GithubURL: https://github.com/Kavya-Divami/kavya-family-league.git

# Family League — local environment variables
# Copy this file to `.env` and fill in real values. Never commit `.env`.

# --- Database ---
DB_URL=jdbc:postgresql://localhost:5432/family_league
DB_USERNAME=your-postgres-user
DB_PASSWORD=your-postgres-password

# --- JPA / Flyway ---
JPA_DDL_AUTO=validate
JPA_SHOW_SQL=false
FLYWAY_ENABLED=true

# --- HTTP server ---
SERVER_PORT=8080

# --- JWT ---
JWT_SECRET=your-secret-key-minimum-32-characters-long
JWT_EXPIRATION_MS=86400000

# --- Mail / SMTP ---
MAIL_HOST=smtp.gmail.com
MAIL_PORT=587
MAIL_USERNAME=your-email@gmail.com
MAIL_PASSWORD=your-app-password
NOTIFICATION_FROM_EMAIL=no-reply@familyleague.com

# --- Prediction Rules ---
LEAGUE_LOCK_HOURS=4
MATCH_LOCK_HOURS=1

# --- Logging ---
APP_LOG_LEVEL=DEBUG

# --- Notifications ---
REMINDER_BEFORE_LOCK_MINUTES=60
