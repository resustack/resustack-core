#!/bin/bash
# K6 부하 테스트 래퍼 스크립트
# 사용법: ./scripts/run-load-test.sh [--scenario load|spike|stress]

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
K6_DIR="$(dirname "$SCRIPT_DIR")"

# .env 파일이 있으면 환경변수 로드
if [ -f "$SCRIPT_DIR/.env" ]; then
    set -a
    source "$SCRIPT_DIR/.env"
    set +a
fi

# 환경변수 기본값
PG_HOST="${PG_HOST:-localhost}"
PG_PORT="${PG_PORT:-5435}"
PG_DB="${PG_DB:-resustack}"
PG_USER="${PG_USER:-resustack}"
PG_PASSWORD="${PG_PASSWORD:-resustack}"
BASE_URL="${BASE_URL:-http://localhost:8080}"
JWT_SECRET="${JWT_SECRET:?ERROR: JWT_SECRET 환경변수를 설정하세요.}"
JWT_TTL_SECONDS="${JWT_TTL_SECONDS:-3600}"
USER_COUNT="${USER_COUNT:-20}"

# 시나리오 파싱
SCENARIO=""
while [[ $# -gt 0 ]]; do
    case $1 in
        --scenario)
            SCENARIO="$2"
            shift 2
            ;;
        *)
            echo "알 수 없는 옵션: $1"
            echo "사용법: $0 [--scenario load|spike|stress]"
            exit 1
            ;;
    esac
done

echo "=== K6 부하 테스트 시작 ==="
echo "API 서버: $BASE_URL"
echo "사용자 수: $USER_COUNT"
if [ -n "$SCENARIO" ]; then
    echo "시나리오: $SCENARIO"
else
    echo "시나리오: 전체 (load, spike, stress)"
fi

# 1. 테스트 사용자 시딩
echo ""
echo "--- Step 1: 테스트 사용자 시딩 ---"
USER_IDS=$("$SCRIPT_DIR/setup.sh")
echo "생성된 사용자 ID: $USER_IDS"

# 2. K6 실행
echo ""
echo "--- Step 2: K6 부하 테스트 실행 ---"

K6_ARGS=(
    run
    -e "USER_IDS=$USER_IDS"
    -e "JWT_SECRET=$JWT_SECRET"
    -e "JWT_TTL_SECONDS=$JWT_TTL_SECONDS"
    -e "BASE_URL=$BASE_URL"
)

if [ -n "$SCENARIO" ]; then
    K6_ARGS+=(-e "SCENARIO=$SCENARIO")
fi

K6_ARGS+=("$K6_DIR/resume-api-load-test.js")

k6 "${K6_ARGS[@]}"
K6_EXIT_CODE=$?

# 3. 테스트 데이터 정리
echo ""
echo "--- Step 3: 테스트 데이터 정리 ---"
"$SCRIPT_DIR/teardown.sh"

echo ""
echo "=== K6 부하 테스트 완료 ==="
exit $K6_EXIT_CODE
