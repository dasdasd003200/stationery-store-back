CREATE TABLE users (
    id             UUID         PRIMARY KEY,
    username       VARCHAR(50)  NOT NULL,
    full_name      VARCHAR(120) NOT NULL,
    email          VARCHAR(150),
    password_hash  VARCHAR(100) NOT NULL,
    role           VARCHAR(20)  NOT NULL,
    active         BOOLEAN      NOT NULL DEFAULT TRUE,
    last_login_at  TIMESTAMPTZ,
    created_at     TIMESTAMPTZ  NOT NULL,
    updated_at     TIMESTAMPTZ  NOT NULL,

    CONSTRAINT uk_users_username UNIQUE (username),
    CONSTRAINT uk_users_email    UNIQUE (email),
    CONSTRAINT ck_users_role     CHECK (role IN ('ADMIN', 'MANAGER', 'EMPLOYEE'))
);

CREATE INDEX idx_users_role_active ON users (role, active);
