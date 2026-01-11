-- liquibase formatted sql

-- changeset jhyoo:1 runInTransaction:false
CREATE TABLE IF NOT EXISTS users (
                                     id                BIGSERIAL PRIMARY KEY   NOT NULL,
                                     status            TEXT                    NOT NULL,
                                     email             TEXT                    NOT NULL,
                                     name              TEXT                    NOT NULL,
                                     profile_image_url TEXT                    NOT NULL,
                                     gender            TEXT                    NOT NULL,
                                     birth_year        TEXT                    NOT NULL,
                                     created_at        TIMESTAMP DEFAULT NOW() NOT NULL,
                                     updated_at        TIMESTAMP
);

COMMENT ON COLUMN users.id IS '사용자 id';
COMMENT ON COLUMN users.status IS '사용자의 status';
COMMENT ON COLUMN users.email IS '사용자의 이메일';
COMMENT ON COLUMN users.name IS '사용자의 이름';
COMMENT ON COLUMN users.profile_image_url IS '사용자의 프로필 사진 URL';
COMMENT ON COLUMN users.gender IS '사용자의 성별';
COMMENT ON COLUMN users.birth_year IS '사용자의 출생연도';
COMMENT ON COLUMN users.created_at IS '데이터 생성일자';
COMMENT ON COLUMN users.updated_at IS '데이터 수정일자';

CREATE UNIQUE INDEX CONCURRENTLY IF NOT EXISTS uidx_users_email ON users (email);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_users_name ON users (name);
