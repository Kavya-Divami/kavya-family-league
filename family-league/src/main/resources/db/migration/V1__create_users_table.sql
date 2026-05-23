-- V1__create_users_table.sql
-- Creates the users table for the Family League platform

CREATE TYPE user_role AS ENUM ('ADMIN', 'USER');

CREATE TABLE users (
    id              UUID            NOT NULL DEFAULT gen_random_uuid() PRIMARY KEY,
    username        VARCHAR(50)     NOT NULL UNIQUE,
    email           VARCHAR(255)    NOT NULL UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    first_name      VARCHAR(100),
    last_name       VARCHAR(100),
    avatar_name     VARCHAR(100),
    profile_pic_url VARCHAR(500),
    role            user_role       NOT NULL DEFAULT 'USER',
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    is_deleted      BOOLEAN         NOT NULL DEFAULT FALSE,
    created_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_by      VARCHAR(255),
    updated_at      TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_by      VARCHAR(255),
    deleted_at      TIMESTAMP WITH TIME ZONE,
    deleted_by      VARCHAR(255)
);

CREATE INDEX idx_users_email     ON users(email)     WHERE is_deleted = FALSE;
CREATE INDEX idx_users_username  ON users(username)  WHERE is_deleted = FALSE;
CREATE INDEX idx_users_role      ON users(role)      WHERE is_deleted = FALSE;
CREATE INDEX idx_users_is_active ON users(is_active) WHERE is_deleted = FALSE;

COMMENT ON TABLE  users                IS 'Platform users with role-based access';
COMMENT ON COLUMN users.password_hash  IS 'BCrypt hashed password — never store plain text';
COMMENT ON COLUMN users.is_deleted     IS 'Soft-delete flag — records are never physically removed';
COMMENT ON COLUMN users.avatar_name    IS 'Display name for the user avatar inside a league';
