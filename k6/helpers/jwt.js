import crypto from 'k6/crypto';
import encoding from 'k6/encoding';

/**
 * Base64URL 인코딩 (패딩 제거, +→-, /→_)
 * JWT 스펙(RFC 7519)에서 요구하는 형식
 */
function base64UrlEncode(str) {
    const b64 = encoding.b64encode(str, 'std');
    return b64.replace(/=/g, '').replace(/\+/g, '-').replace(/\//g, '_');
}

/**
 * 키 크기에 따른 HMAC 알고리즘 자동 선택
 *
 * jjwt의 signWith(key)와 동일한 로직:
 * - 256비트(32바이트) → HS256
 * - 384비트(48바이트) → HS384
 * - 512비트(64바이트) 이상 → HS512
 */
function resolveAlgorithm(keyBytes) {
    const bitLen = keyBytes.byteLength * 8;
    if (bitLen >= 512) return { alg: 'HS512', hash: 'sha512' };
    if (bitLen >= 384) return { alg: 'HS384', hash: 'sha384' };
    return { alg: 'HS256', hash: 'sha256' };
}

/**
 * JWT Access Token 생성
 *
 * JwtTokenGeneratorImpl과 동일한 클레임 구조 재현:
 * - sub: email
 * - uid: userId
 * - auth: authorities (쉼표 구분 문자열)
 * - iat/exp: 발급/만료 시각
 * - 서명: 키 크기에 따라 HS256/HS384/HS512 자동 선택 (jjwt signWith 동작 재현)
 */
export function generateAccessToken(userId, email, secret, ttlSeconds) {
    const keyBytes = encoding.b64decode(secret, 'std');
    const { alg, hash } = resolveAlgorithm(keyBytes);

    const header = { alg: alg, typ: 'JWT' };
    const now = Math.floor(Date.now() / 1000);
    const payload = {
        sub: email,
        uid: userId,
        auth: 'ROLE_USER',
        iat: now,
        exp: now + ttlSeconds,
    };

    const headerB64 = base64UrlEncode(JSON.stringify(header));
    const payloadB64 = base64UrlEncode(JSON.stringify(payload));
    const signingInput = `${headerB64}.${payloadB64}`;

    const signature = crypto.hmac(hash, keyBytes, signingInput, 'base64rawurl');

    return `${signingInput}.${signature}`;
}
