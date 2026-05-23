-- V2__create_domain_tables.sql
-- Creates all domain tables for Family League platform

CREATE TYPE league_status AS ENUM ('ACTIVE', 'CLOSED');
CREATE TYPE season_status AS ENUM ('UPCOMING', 'ACTIVE', 'COMPLETED', 'CLOSED');
CREATE TYPE match_status  AS ENUM ('SCHEDULED', 'COMPLETED', 'CANCELLED');
CREATE TYPE email_event_type AS ENUM ('MATCH_PREDICTION_REMINDER', 'LEAGUE_PREDICTION_REMINDER', 'RESULT_UPDATE_ALERT', 'BULK_COMMUNICATION');
CREATE TYPE email_status AS ENUM ('PENDING', 'SENT', 'FAILED');

-- Leagues (umbrella concept)
CREATE TABLE leagues (
    id          UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(150)    NOT NULL,
    description VARCHAR(500),
    status      league_status   NOT NULL DEFAULT 'ACTIVE',
    is_deleted  BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

-- Seasons (instances of a league)
CREATE TABLE seasons (
    id                          UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    league_id                   UUID            NOT NULL REFERENCES leagues(id),
    season_number               INTEGER         NOT NULL,
    start_date                  TIMESTAMP WITH TIME ZONE,
    end_date                    TIMESTAMP WITH TIME ZONE,
    first_match_starts_at       TIMESTAMP WITH TIME ZONE,
    league_prediction_lock_at   TIMESTAMP WITH TIME ZONE,
    status                      season_status   NOT NULL DEFAULT 'UPCOMING',
    is_deleted                  BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    UNIQUE (league_id, season_number)
);

-- Teams (independent of seasons)
CREATE TABLE teams (
    id          UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    name        VARCHAR(150)    NOT NULL,
    short_code  VARCHAR(10),
    logo_url    VARCHAR(500),
    is_deleted  BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

-- Players (belong to a team)
CREATE TABLE players (
    id              UUID        NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    team_id         UUID        NOT NULL REFERENCES teams(id),
    name            VARCHAR(150) NOT NULL,
    jersey_number   INTEGER,
    is_deleted      BOOLEAN     NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

-- Season-Team mapping (which teams play in a season)
CREATE TABLE season_teams (
    id          UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    season_id   UUID    NOT NULL REFERENCES seasons(id),
    team_id     UUID    NOT NULL REFERENCES teams(id),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    UNIQUE (season_id, team_id)
);

-- Matches
CREATE TABLE matches (
    id              UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    season_id       UUID            NOT NULL REFERENCES seasons(id),
    home_team_id    UUID            NOT NULL REFERENCES teams(id),
    away_team_id    UUID            NOT NULL REFERENCES teams(id),
    match_number    INTEGER,
    venue           VARCHAR(255),
    scheduled_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    lock_at         TIMESTAMP WITH TIME ZONE NOT NULL,
    status          match_status    NOT NULL DEFAULT 'SCHEDULED',
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

CREATE INDEX idx_matches_season   ON matches(season_id) WHERE is_deleted = FALSE;
CREATE INDEX idx_matches_lock_at  ON matches(lock_at)   WHERE status = 'SCHEDULED';

-- Match results
CREATE TABLE match_results (
    id                      UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    match_id                UUID    NOT NULL REFERENCES matches(id) UNIQUE,
    winner_team_id          UUID    REFERENCES teams(id),
    toss_winner_team_id     UUID    REFERENCES teams(id),
    player_of_match_id      UUID    REFERENCES players(id),
    is_tie                  BOOLEAN NOT NULL DEFAULT FALSE,
    published_at            TIMESTAMP WITH TIME ZONE,
    published_by            VARCHAR(255),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

-- League predictions (user predicts final team standings for a season)
CREATE TABLE league_predictions (
    id              UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         UUID    NOT NULL REFERENCES users(id),
    season_id       UUID    NOT NULL REFERENCES seasons(id),
    submitted_at    TIMESTAMP WITH TIME ZONE,
    is_locked       BOOLEAN NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    UNIQUE (user_id, season_id)
);

-- League prediction items (position -> team)
CREATE TABLE league_prediction_items (
    id                      UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    league_prediction_id    UUID    NOT NULL REFERENCES league_predictions(id),
    position                INTEGER NOT NULL,
    team_id                 UUID    NOT NULL REFERENCES teams(id),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    UNIQUE (league_prediction_id, position)
);

-- Match predictions
CREATE TABLE match_predictions (
    id                              UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id                         UUID    NOT NULL REFERENCES users(id),
    match_id                        UUID    NOT NULL REFERENCES matches(id),
    predicted_winner_team_id        UUID    REFERENCES teams(id),
    predicted_toss_winner_team_id   UUID    REFERENCES teams(id),
    predicted_potm_player_id        UUID    REFERENCES players(id),
    submitted_at                    TIMESTAMP WITH TIME ZONE,
    is_locked                       BOOLEAN NOT NULL DEFAULT FALSE,
    points_awarded                  INTEGER NOT NULL DEFAULT 0,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    UNIQUE (user_id, match_id)
);

CREATE INDEX idx_match_predictions_match  ON match_predictions(match_id);
CREATE INDEX idx_match_predictions_user   ON match_predictions(user_id);

-- Season leaderboard
CREATE TABLE season_leaderboards (
    id              UUID    NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    user_id         UUID    NOT NULL REFERENCES users(id),
    season_id       UUID    NOT NULL REFERENCES seasons(id),
    total_points    INTEGER NOT NULL DEFAULT 0,
    rank            INTEGER,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255),
    UNIQUE (user_id, season_id)
);

CREATE INDEX idx_leaderboard_season ON season_leaderboards(season_id);

-- Email logs
CREATE TABLE email_logs (
    id                      UUID                NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    recipient_user_id       UUID                REFERENCES users(id),
    recipient_email         VARCHAR(255)        NOT NULL,
    event_type              email_event_type    NOT NULL,
    subject                 VARCHAR(500)        NOT NULL,
    body                    TEXT,
    status                  email_status        NOT NULL DEFAULT 'PENDING',
    sent_at                 TIMESTAMP WITH TIME ZONE,
    error_message           VARCHAR(1000),
    related_entity_id       UUID,
    related_entity_type     VARCHAR(100),
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by  VARCHAR(255),
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by  VARCHAR(255)
);

CREATE INDEX idx_email_logs_event_type ON email_logs(event_type);
CREATE INDEX idx_email_logs_status     ON email_logs(status);
