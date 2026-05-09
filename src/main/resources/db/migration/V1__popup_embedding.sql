CREATE EXTENSION IF NOT EXISTS vector;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'popup'
          AND column_name = 'originId'
    ) THEN
        ALTER TABLE popup RENAME COLUMN "originId" TO origin_id;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'popup'
          AND column_name = 'strat_date'
    ) THEN
        ALTER TABLE popup RENAME COLUMN strat_date TO start_date;
    END IF;
END $$;

DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'popup'
          AND column_name = 'reservationi_url'
    ) THEN
        ALTER TABLE popup RENAME COLUMN reservationi_url TO reservation_url;
    END IF;
END $$;

ALTER TABLE popup
    ADD COLUMN IF NOT EXISTS content_hash VARCHAR(64);

ALTER TABLE popup
    ADD COLUMN IF NOT EXISTS main_brand VARCHAR(255);

ALTER TABLE popup
    ADD COLUMN IF NOT EXISTS hashtags TEXT;

ALTER TABLE popup
    ADD COLUMN IF NOT EXISTS status VARCHAR(50);

CREATE TABLE IF NOT EXISTS popup_embedding (
    id BIGSERIAL PRIMARY KEY,
    popup_id BIGINT NOT NULL UNIQUE REFERENCES popup(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    embedding vector(768) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);
