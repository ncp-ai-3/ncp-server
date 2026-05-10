CREATE TABLE IF NOT EXISTS refresh_token (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    token TEXT NOT NULL UNIQUE,
    expires_at TIMESTAMP NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_refresh_token_member_id
    ON refresh_token(member_id);

CREATE INDEX IF NOT EXISTS idx_refresh_token_member_id_revoked
    ON refresh_token(member_id, revoked);
