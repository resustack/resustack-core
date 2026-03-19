import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend } from 'k6/metrics';
import { randomItem } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

import { generateAccessToken } from './helpers/jwt.js';
import { API_BASE_URL, JWT_SECRET, JWT_TTL_SECONDS, USER_IDS, getAuthHeaders } from './helpers/config.js';
import { generateResumeCreatePayload, generateResumeUpdatePayload } from './helpers/payload.js';

// ── Custom Metrics ──────────────────────────────────────────
const resumeListLatency = new Trend('resume_list_latency', true);
const resumeDetailLatency = new Trend('resume_detail_latency', true);
const resumeCreateLatency = new Trend('resume_create_latency', true);
const resumeUpdateLatency = new Trend('resume_update_latency', true);
const resumeDeleteLatency = new Trend('resume_delete_latency', true);

// ── 시나리오 정의 ────────────────────────────────────────────
const allScenarios = {
    // Load Test: 목표 TPS(50 req/s) 안정성 검증
    load: {
        executor: 'constant-arrival-rate',
        rate: 50,
        timeUnit: '1s',
        duration: '2m',
        preAllocatedVUs: 60,
        maxVUs: 100,
        exec: 'resumeScenario',
    },
    // Spike Test: 트래픽 급증 대응
    spike: {
        executor: 'ramping-arrival-rate',
        startRate: 10,
        timeUnit: '1s',
        preAllocatedVUs: 100,
        maxVUs: 300,
        stages: [
            { duration: '10s', target: 10 },
            { duration: '20s', target: 200 },
            { duration: '30s', target: 200 },
            { duration: '10s', target: 10 },
        ],
        exec: 'resumeScenario',
    },
    // Stress Test: 한계점 탐색 (최대 1000 req/s)
    stress: {
        executor: 'ramping-arrival-rate',
        startRate: 10,
        timeUnit: '1s',
        preAllocatedVUs: 200,
        maxVUs: 1500,
        stages: [
            { duration: '20s', target: 100 },
            { duration: '20s', target: 200 },
            { duration: '20s', target: 400 },
            { duration: '20s', target: 600 },
            { duration: '20s', target: 800 },
            { duration: '20s', target: 1000 },
            { duration: '20s', target: 10 },
        ],
        exec: 'resumeScenario',
    },
};

// SCENARIO 환경변수로 개별 시나리오 선택 (미지정 시 전체 실행)
const selectedScenario = __ENV.SCENARIO;
const scenarios = selectedScenario
    ? { [selectedScenario]: allScenarios[selectedScenario] }
    : allScenarios;

export const options = {
    scenarios,
    thresholds: {
        // 전체 기준
        'http_req_duration': ['p(95)<500', 'p(99)<1000'],
        'http_req_failed': ['rate<0.01'],
        // API별 기준
        'resume_list_latency': ['p(95)<300'],
        'resume_detail_latency': ['p(95)<200'],
        'resume_create_latency': ['p(95)<500'],
        'resume_update_latency': ['p(95)<500'],
        'resume_delete_latency': ['p(95)<500'],
        // 시나리오별 기준
        'http_req_duration{scenario:load}': ['p(95)<500'],
        'http_req_duration{scenario:spike}': ['p(95)<800'],
        'http_req_duration{scenario:stress}': ['p(95)<1000'],
    },
};

// ── Setup: 토큰 풀 생성 + 템플릿 ID 조회 ────────────────────
export function setup() {
    if (!JWT_SECRET) {
        console.error('ERROR: JWT_SECRET 환경변수가 설정되지 않았습니다.');
        return null;
    }
    if (USER_IDS.length === 0) {
        console.error('ERROR: USER_IDS 환경변수가 설정되지 않았습니다.');
        return null;
    }

    // 각 userId에 대해 JWT Access Token 생성
    const tokens = [];
    const userIds = [];
    for (const rawId of USER_IDS) {
        const userId = parseInt(rawId.trim());
        const email = `loadtest-${userId}@test.com`;
        const token = generateAccessToken(userId, email, JWT_SECRET, JWT_TTL_SECONDS);
        tokens.push(token);
        userIds.push(userId);
    }
    console.log(`토큰 풀 생성 완료: ${tokens.length}개`);

    // 활성 템플릿 ID 조회 (리다이렉트 방지: oauth2Login이 302 → HTML 로그인 페이지 반환 방지)
    const params = getAuthHeaders(tokens[0]);
    params.redirects = 0;
    const res = http.get(`${API_BASE_URL}/api/templates?status=ACTIVE`, params);

    if (res.status !== 200) {
        console.error(`ERROR: 템플릿 목록 조회 실패. Status: ${res.status}`);
        console.error(`Response Body (처음 500자): ${res.body ? res.body.substring(0, 500) : '(empty)'}`);
        return null;
    }

    let templates;
    try {
        templates = res.json().data;
    } catch (e) {
        console.error(`ERROR: 응답 JSON 파싱 실패. Content-Type: ${res.headers['Content-Type']}`);
        console.error(`Response Body (처음 500자): ${res.body ? res.body.substring(0, 500) : '(empty)'}`);
        return null;
    }

    if (!templates || templates.length === 0) {
        console.error('ERROR: 활성화된 템플릿이 없습니다.');
        return null;
    }

    const templateId = templates[0].id;
    console.log(`Setup 완료. 템플릿 ID: ${templateId}, 사용자 수: ${userIds.length}`);

    return { tokens, userIds, templateId };
}

// ── 메인 시나리오: 확률 기반 API 호출 ────────────────────────
export function resumeScenario(data) {
    if (!data || !data.templateId) {
        sleep(1);
        return;
    }

    // VU별 독립 토큰 할당
    const tokenIndex = __VU % data.tokens.length;
    const token = data.tokens[tokenIndex];
    const params = getAuthHeaders(token);

    const roll = Math.random();

    if (roll < 0.40) {
        // 목록 조회 (40%)
        const res = http.get(`${API_BASE_URL}/api/resumes`, params);
        check(res, { '목록 조회 성공 (200)': (r) => r.status === 200 });
        resumeListLatency.add(res.timings.duration);

    } else if (roll < 0.70) {
        // 상세 조회 (30%) — 먼저 목록에서 ID 획득
        const listRes = http.get(`${API_BASE_URL}/api/resumes`, params);
        resumeListLatency.add(listRes.timings.duration);

        let resumes = [];
        try {
            const body = listRes.json();
            if (body && body.data && body.data.content) {
                resumes = body.data.content;
            } else if (body && body.data && Array.isArray(body.data)) {
                resumes = body.data;
            }
        } catch (_) { /* 파싱 실패 무시 */ }

        if (resumes.length > 0) {
            const target = randomItem(resumes);
            const detailRes = http.get(`${API_BASE_URL}/api/resumes/${target.id}`, params);
            check(detailRes, { '상세 조회 성공 (200)': (r) => r.status === 200 });
            resumeDetailLatency.add(detailRes.timings.duration);
        }

    } else if (roll < 0.85) {
        // 생성 (15%)
        const payload = generateResumeCreatePayload(data.templateId);
        const res = http.post(`${API_BASE_URL}/api/resumes`, payload, params);
        check(res, { '이력서 생성 성공 (201)': (r) => r.status === 201 });
        resumeCreateLatency.add(res.timings.duration);

    } else if (roll < 0.95) {
        // 수정 (10%) — 먼저 목록에서 ID 획득
        const listRes = http.get(`${API_BASE_URL}/api/resumes`, params);

        let resumes = [];
        try {
            const body = listRes.json();
            if (body && body.data && body.data.content) {
                resumes = body.data.content;
            } else if (body && body.data && Array.isArray(body.data)) {
                resumes = body.data;
            }
        } catch (_) { /* 파싱 실패 무시 */ }

        if (resumes.length > 0) {
            const target = randomItem(resumes);
            const payload = generateResumeUpdatePayload();
            const res = http.put(`${API_BASE_URL}/api/resumes/${target.id}`, payload, params);
            check(res, { '이력서 수정 성공 (200)': (r) => r.status === 200 });
            resumeUpdateLatency.add(res.timings.duration);
        }

    } else {
        // 삭제 (5%) — 새로 생성 후 삭제
        const createPayload = generateResumeCreatePayload(data.templateId);
        const createRes = http.post(`${API_BASE_URL}/api/resumes`, createPayload, params);

        if (createRes.status === 201) {
            let createdId = null;
            try {
                createdId = createRes.json().data.id;
            } catch (_) { /* 파싱 실패 무시 */ }

            if (createdId) {
                const deleteRes = http.del(`${API_BASE_URL}/api/resumes/${createdId}`, null, params);
                check(deleteRes, { '이력서 삭제 성공 (200)': (r) => r.status === 200 });
                resumeDeleteLatency.add(deleteRes.timings.duration);
            }
        }
    }

    // 현실적 요청 간격
    sleep(Math.random() * 0.5);
}

// ── Teardown: 로그 출력 (실제 정리는 teardown.sh가 담당) ─────
export function teardown(data) {
    if (data) {
        console.log(`테스트 종료. 사용자 ${data.userIds.length}명의 토큰으로 테스트 수행 완료.`);
        console.log('테스트 데이터 정리는 teardown.sh를 실행하세요.');
    }
}
