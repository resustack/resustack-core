#!/bin/bash
# PostgreSQL에 부하 테스트용 사용자 시딩
# 생성된 user ID 목록을 stdout으로 출력 (쉼표 구분)

set -euo pipefail

PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-5435}"
PG_DB="${PG_DB:-resustack}"
PG_USER="${PG_USER:-resustack}"
PG_PASSWORD="${PG_PASSWORD:-resustack}"
USER_COUNT="${USER_COUNT:-20}"

export PGPASSWORD="$PG_PASSWORD"

# 테스트 사용자 INSERT (ON CONFLICT로 멱등성 보장)
for i in $(seq 1 "$USER_COUNT"); do
    psql -h "$PG_HOST" -p "$PG_PORT" -d "$PG_DB" -U "$PG_USER" -tAq <<EOF
INSERT INTO users (email, name, profile_image_url, gender, birth_year, status)
VALUES (
    'loadtest-${i}@test.com',
    'LoadTest User ${i}',
    'https://test.com/profile.png',
    'MALE',
    '1990',
    'ACTIVE'
)
ON CONFLICT (email) DO NOTHING;
EOF
done

# 생성된 사용자 ID 조회 (쉼표 구분 출력)
USER_IDS=$(psql -h "$PG_HOST" -p "$PG_PORT" -d "$PG_DB" -U "$PG_USER" -tAq <<EOF
SELECT string_agg(id::text, ',' ORDER BY id)
FROM users
WHERE email LIKE 'loadtest-%@test.com';
EOF
)

echo "$USER_IDS"
