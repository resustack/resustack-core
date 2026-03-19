#!/bin/bash
# 부하 테스트 데이터 정리
# PostgreSQL: loadtest 사용자 삭제
# MongoDB: 해당 userId의 이력서 삭제

set -euo pipefail

PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-5435}"
PG_DB="${PG_DB:-resustack}"
PG_USER="${PG_USER:-resustack}"
PG_PASSWORD="${PG_PASSWORD:-resustack}"

MONGO_URI="${MONGO_URI:-mongodb://resustack:resustack@localhost:27018/resustack?authSource=admin}"

export PGPASSWORD="$PG_PASSWORD"

echo "=== 테스트 데이터 정리 시작 ==="

# 1. MongoDB에서 테스트 사용자의 이력서 삭제
# 먼저 PostgreSQL에서 테스트 사용자 ID 목록 조회
USER_IDS=$(psql -h "$PG_HOST" -p "$PG_PORT" -d "$PG_DB" -U "$PG_USER" -tAq <<EOF
SELECT string_agg(id::text, ',')
FROM users
WHERE email LIKE 'loadtest-%@test.com';
EOF
)

if [ -n "$USER_IDS" ]; then
    # 쉼표 구분 ID를 MongoDB 숫자 배열로 변환 (userId는 Long 타입)
    MONGO_ID_ARRAY=$(echo "$USER_IDS" | tr ',' ',')

    mongosh "$MONGO_URI" --quiet --eval "
            const result = db.resumes.deleteMany({ userId: { \$in: [$MONGO_ID_ARRAY] } });
            print('MongoDB 이력서 삭제: ' + result.deletedCount + '건');
        "
fi

# 2. PostgreSQL에서 테스트 사용자 삭제
DELETED_COUNT=$(psql -h "$PG_HOST" -p "$PG_PORT" -d "$PG_DB" -U "$PG_USER" -tAq <<EOF
WITH deleted AS (
    DELETE FROM users WHERE email LIKE 'loadtest-%@test.com' RETURNING *
)
SELECT count(*) FROM deleted;
EOF
)

echo "PostgreSQL 사용자 삭제: ${DELETED_COUNT}건"
echo "=== 테스트 데이터 정리 완료 ==="
