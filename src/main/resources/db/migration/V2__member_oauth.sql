ALTER TABLE member
    ADD COLUMN IF NOT EXISTS name VARCHAR(100);

CREATE TABLE IF NOT EXISTS member_oauth (
    id BIGSERIAL PRIMARY KEY,
    member_id BIGINT NOT NULL,
    oauth_provider VARCHAR(20) NOT NULL,
    provider_id VARCHAR(512) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now(),
    CONSTRAINT uk_member_oauth_provider_provider_id UNIQUE (oauth_provider, provider_id)
);

CREATE INDEX IF NOT EXISTS idx_member_oauth_member_id
    ON member_oauth(member_id);
