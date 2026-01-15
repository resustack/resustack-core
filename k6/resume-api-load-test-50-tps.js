import http from 'k6/http';
import { check, sleep } from 'k6';
import { randomItem } from 'https://jslib.k6.io/k6-utils/1.2.0/index.js';

// 옵션 설정: 50 TPS 검증
export const options = {
    stages: [
        { duration: '10s', target: 50 }, // 10초 동안 VU 50까지 증가
        { duration: '30s', target: 50 },  // 30초 동안 VU 50 유지
        { duration: '10s', target: 0 },  // 10초 동안 VU 0으로 감소
    ],
    thresholds: {
        http_req_duration: ['p(95)<500'], // 95%의 요청이 500ms 이내 완료
        http_req_failed: ['rate<0.01'],   // 에러율 1% 미만
    },
};

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
const ACCESS_TOKEN = __ENV.ACCESS_TOKEN;

// 헤더 설정
const params = {
    headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${ACCESS_TOKEN}`,
    },
};

// 템플릿 ID를 가져오기 위한 setup 함수
export function setup() {
    if (!ACCESS_TOKEN) {
        console.error('ERROR: ACCESS_TOKEN 환경변수가 설정되지 않았습니다.');
        return { templateId: null };
    }

    const res = http.get(`${BASE_URL}/api/templates?status=ACTIVE`, params);
    if (res.status !== 200) {
        console.error(`ERROR: 템플릿 목록 조회 실패. Status: ${res.status} Body: ${res.body}`);
        return { templateId: null };
    }

    const responseBody = res.json();
    const templates = responseBody.data;

    if (!templates || templates.length === 0) {
        console.error('ERROR: 활성화된 템플릿이 없습니다.');
        return { templateId: null };
    }

    const templateId = templates[0].id;
    console.log(`Setup 완료. 사용 템플릿 ID: ${templateId}`);
    return { templateId };
}

export default function (data) {
    if (!data || !data.templateId) {
        // Setup 실패 시 실행 중단
        sleep(1);
        return;
    }

    // 1. 내 이력서 목록 조회
    const listRes = http.get(`${BASE_URL}/api/resumes`, params);

    check(listRes, {
        '목록 조회 성공 (200)': (r) => r.status === 200,
    });

    // 응답 파싱 시 에러 방지
    let myResumes = [];
    try {
        const body = listRes.json();
        if (body && body.data) {
            myResumes = body.data;
        }
    } catch (e) {
        // JSON 파싱 실패 등
    }

    // 2. 이력서 상세 조회 (목록이 있으면 랜덤 조회)
    // 80% 확률로 상세 조회 시도
    if (myResumes.length > 0 && Math.random() < 0.8) {
        const randomResume = randomItem(myResumes);
        const detailRes = http.get(`${BASE_URL}/api/resumes/${randomResume.id}`, params);

        check(detailRes, {
            '상세 조회 성공 (200)': (r) => r.status === 200,
        });
    }

    // 3. 이력서 생성 (20% 확률 혹은 목록이 없을 때)
    if (myResumes.length === 0 || Math.random() < 0.2) {
        const payload = JSON.stringify({
            title: `Load Test Resume ${Date.now()}`,
            templateId: data.templateId,
            profile: {
                name: "Load Tester",
                position: "Software Engineer",
                introduction: "K6 Load Testing...",
                // contact 정보는 선택사항이므로 없어도 됨
            },
            sections: [],
            isPublic: true
        });

        const createRes = http.post(`${BASE_URL}/api/resumes`, payload, params);

        check(createRes, {
            '이력서 생성 성공 (201)': (r) => r.status === 201,
        });
    }

    sleep(1);
}
