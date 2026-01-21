package com.resustack.common.security.cookie

import jakarta.servlet.http.Cookie
import org.springframework.http.ResponseCookie

/**
 * JWT 토큰 쿠키 생성 및 추출을 위한 유틸리티 클래스
 */
object CookieUtils {
    private const val ACCESS_TOKEN_COOKIE_NAME = "accessToken"
    private const val REFRESH_TOKEN_COOKIE_NAME = "refreshToken"
    private const val EMPTY_STRING = ""

    /**
     * Access Token용 HTTP-Only 쿠키 생성
     *
     * @param token JWT Access Token
     * @param maxAge 쿠키 유효 시간 (초)
     * @param properties 쿠키 설정 (domain, secure, sameSite)
     * @return ResponseCookie 객체
     */
    fun createAccessTokenCookie(
        token: String,
        maxAge: Long,
        properties: CookieProperties
    ): ResponseCookie {
        return createCookie(ACCESS_TOKEN_COOKIE_NAME, token, maxAge, properties)
    }

    /**
     * Refresh Token용 HTTP-Only 쿠키 생성
     *
     * @param token JWT Refresh Token
     * @param maxAge 쿠키 유효 시간 (초)
     * @param properties 쿠키 설정 (domain, secure, sameSite)
     * @return ResponseCookie 객체
     */
    fun createRefreshTokenCookie(
        token: String,
        maxAge: Long,
        properties: CookieProperties
    ): ResponseCookie {
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, token, maxAge, properties)
    }

    /**
     * ResponseCookie를 사용한 쿠키 생성 (SameSite 속성 지원)
     *
     * @param name 쿠키 이름
     * @param value 쿠키 값
     * @param maxAge 쿠키 유효 시간 (초)
     * @param properties 쿠키 설정
     * @return ResponseCookie 객체
     */
    private fun createCookie(
        name: String,
        value: String,
        maxAge: Long,
        properties: CookieProperties
    ): ResponseCookie {
        val builder = ResponseCookie.from(name, value)
            .httpOnly(true)           // JavaScript 접근 차단
            .secure(properties.secure) // HTTPS에서만 전송 (프로덕션: true)
            .path("/")                // 모든 경로에서 전송
            .maxAge(maxAge)           // 쿠키 유효 시간 (초)
            .sameSite(properties.sameSite) // CSRF 방어 (Lax or Strict)

        // 도메인이 설정되어 있으면 추가
        properties.domain?.let { builder.domain(it) }

        return builder.build()
    }

    /**
     * Access Token 쿠키 삭제
     *
     * @param properties 쿠키 설정 (domain, secure, sameSite)
     * @return 삭제용 ResponseCookie 객체 (maxAge=0)
     */
    fun deleteAccessTokenCookie(properties: CookieProperties): ResponseCookie {
        return createCookie(ACCESS_TOKEN_COOKIE_NAME, EMPTY_STRING, 0, properties)
    }

    /**
     * Refresh Token 쿠키 삭제
     *
     * @param properties 쿠키 설정 (domain, secure, sameSite)
     * @return 삭제용 ResponseCookie 객체 (maxAge=0)
     */
    fun deleteRefreshTokenCookie(properties: CookieProperties): ResponseCookie {
        return createCookie(REFRESH_TOKEN_COOKIE_NAME, EMPTY_STRING, 0, properties)
    }

    /**
     * 쿠키에서 Access Token 추출
     *
     * @param cookies HttpServletRequest의 쿠키 배열
     * @return Access Token 문자열 (없으면 null)
     */
    fun getAccessTokenFromCookies(cookies: Array<Cookie>?): String? {
        return cookies?.firstOrNull { it.name == ACCESS_TOKEN_COOKIE_NAME }?.value
    }

    /**
     * 쿠키에서 Refresh Token 추출
     *
     * @param cookies HttpServletRequest의 쿠키 배열
     * @return Refresh Token 문자열 (없으면 null)
     */
    fun getRefreshTokenFromCookies(cookies: Array<Cookie>?): String? {
        return cookies?.firstOrNull { it.name == REFRESH_TOKEN_COOKIE_NAME }?.value
    }
}
