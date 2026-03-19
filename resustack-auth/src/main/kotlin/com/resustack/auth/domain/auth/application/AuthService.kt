package com.resustack.auth.domain.auth.application

import com.resustack.auth.domain.auth.application.dto.UserInfoResponse
import com.resustack.auth.global.exception.AuthorizationException
import com.resustack.auth.global.security.jwt.JwtTokenGenerator
import com.resustack.common.domain.user.UserRepository
import com.resustack.common.model.ErrorCode
import com.resustack.common.security.cookie.CookieProperties
import com.resustack.common.security.cookie.CookieUtils
import com.resustack.common.security.jwt.JwtProperties
import com.resustack.common.security.jwt.JwtTokenProvider
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val cookieProperties: CookieProperties,
    private val jwtProperties: JwtProperties,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtTokenGenerator: JwtTokenGenerator,
    private val refreshTokenService: RefreshTokenService
) {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional(readOnly = true)
    fun getUserInfo(userId: Long): UserInfoResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { AuthorizationException(ErrorCode.RESOURCE_NOT_FOUND) }

        return UserInfoResponse.from(user)
    }

    /**
     * Refresh Token으로 새 토큰 쌍 발급 (Refresh Token Rotation)
     * Redis에 저장된 토큰과 비교하여 탈취 감지
     */
    fun refresh(request: HttpServletRequest, response: HttpServletResponse) {
        // 1. 쿠키에서 refreshToken 추출
        val refreshToken = CookieUtils.getRefreshTokenFromCookies(request.cookies)
            ?: throw AuthorizationException(ErrorCode.REFRESH_TOKEN_EXPIRED)

        // 2. JWT 유효성 검증
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw AuthorizationException(ErrorCode.REFRESH_TOKEN_EXPIRED)
        }

        // 3. userId 추출
        val userId = jwtTokenProvider.getUserIdFromToken(refreshToken)
            ?: throw AuthorizationException(ErrorCode.REFRESH_TOKEN_INVALID)

        // 4. Redis에 저장된 토큰과 비교 (탈취 감지)
        val storedToken = refreshTokenService.getRefreshToken(userId)
        if (storedToken == null || storedToken != refreshToken) {
            // 불일치 시 탈취 의심 → 해당 userId 토큰 삭제
            refreshTokenService.deleteRefreshToken(userId)
            log.warn("Refresh token 불일치 감지 - 탈취 의심. userId: {}", userId)
            throw AuthorizationException(ErrorCode.REFRESH_TOKEN_INVALID)
        }

        // 5. 사용자 정보 조회
        val user = userRepository.findById(userId)
            .orElseThrow { AuthorizationException(ErrorCode.RESOURCE_NOT_FOUND) }

        // 6. 새 토큰 쌍 발급
        val tokenResponse = jwtTokenGenerator.generateToken(
            userId = user.id!!,
            email = user.email,
            authorities = listOf("ROLE_USER")
        )

        // 7. Redis에 새 Refresh Token 저장 (Rotation)
        refreshTokenService.rotateRefreshToken(userId, tokenResponse.refreshToken)

        // 8. 새 쿠키로 응답
        val accessTokenCookie = CookieUtils.createAccessTokenCookie(
            token = tokenResponse.accessToken,
            maxAge = jwtProperties.accessTokenValidityInSeconds,
            properties = cookieProperties
        )
        val refreshTokenCookie = CookieUtils.createRefreshTokenCookie(
            token = tokenResponse.refreshToken,
            maxAge = jwtProperties.refreshTokenValidityInSeconds,
            properties = cookieProperties
        )

        response.addHeader("Set-Cookie", accessTokenCookie.toString())
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())
    }

    fun logout(userId: Long, response: HttpServletResponse) {
        // Redis에서 Refresh Token 삭제
        refreshTokenService.deleteRefreshToken(userId)

        val accessTokenCookie = CookieUtils.deleteAccessTokenCookie(cookieProperties)
        val refreshTokenCookie = CookieUtils.deleteRefreshTokenCookie(cookieProperties)

        response.addHeader("Set-Cookie", accessTokenCookie.toString())
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        SecurityContextHolder.clearContext()
    }
}
