/**
 * 공통 설정 및 환경변수 로드
 */

export const API_BASE_URL = __ENV.BASE_URL || 'http://localhost:8080';
export const JWT_SECRET = __ENV.JWT_SECRET;
export const JWT_TTL_SECONDS = parseInt(__ENV.JWT_TTL_SECONDS || '3600');
export const USER_IDS = __ENV.USER_IDS ? __ENV.USER_IDS.split(',') : [];

/**
 * 인증 헤더 생성
 */
export function getAuthHeaders(token) {
    return {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
        },
        redirects: 0, // Spring Security oauth2Login의 302 리다이렉트 방지
    };
}
