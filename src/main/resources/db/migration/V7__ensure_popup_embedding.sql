-- V1 이 이미 적용된 DB 에서 popup_embedding 만 없거나 재생성이 필요할 때를 대비한 보강 마이그레이션.
-- Flyway 는 동일 버전 스크립트를 재실행하지 않으므로, 테이블 생성은 새 버전으로만 반영됩니다.

CREATE EXTENSION IF NOT EXISTS vector;

CREATE TABLE IF NOT EXISTS popup_embedding (
    id BIGSERIAL PRIMARY KEY,
    popup_id BIGINT NOT NULL UNIQUE REFERENCES popup(id) ON DELETE CASCADE,
    content TEXT NOT NULL,
    embedding vector(768) NOT NULL,
    created_at TIMESTAMP DEFAULT now(),
    updated_at TIMESTAMP DEFAULT now()
);
