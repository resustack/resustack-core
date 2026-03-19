/**
 * 이력서 API 요청 페이로드 팩토리
 *
 * ResumeCreateRequest / ResumeUpdateRequest DTO 구조에 맞춰 생성
 */

/**
 * 이력서 생성 요청 바디
 */
export function generateResumeCreatePayload(templateId) {
    return JSON.stringify({
        title: `Load Test Resume ${Date.now()}`,
        templateId: templateId,
        profile: {
            name: 'Load Tester',
            position: 'Software Engineer',
            introduction: 'K6 부하 테스트용 이력서입니다.',
        },
        sections: [],
        isPublic: false,
    });
}

/**
 * 이력서 수정 요청 바디
 */
export function generateResumeUpdatePayload() {
    return JSON.stringify({
        title: `Updated Resume ${Date.now()}`,
        profile: {
            name: 'Load Tester',
            position: 'Senior Software Engineer',
            introduction: 'K6 부하 테스트 — 수정된 이력서입니다.',
        },
        sections: [],
        isPublic: false,
    });
}
