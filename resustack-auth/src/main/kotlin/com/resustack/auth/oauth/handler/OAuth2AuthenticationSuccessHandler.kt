package com.resustack.auth.oauth.handler

import com.resustack.auth.global.security.jwt.JwtTokenGenerator
import com.resustack.common.security.cookie.CookieProperties
import com.resustack.common.security.cookie.CookieUtils
import com.resustack.common.security.jwt.JwtProperties
import com.resustack.common.security.principal.PrincipalDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenGenerator: JwtTokenGenerator,
    private val cookieProperties: CookieProperties,
    private val jwtProperties: JwtProperties
) : AuthenticationSuccessHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${app.oauth2.redirect-uri:http://localhost:3000/auth/callback}")
    private lateinit var redirectUri: String

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        val principal = authentication.principal as PrincipalDetails
        val user = principal.getUser()

        log.info("OAuth2 authentication successful for user: ${user.email}")

        val authorities = principal.authorities
            .map(GrantedAuthority::getAuthority)
            .filterNotNull()
            .toList()

        val tokenResponse = jwtTokenGenerator.generateToken(
            userId = user.id!!,
            email = user.email,
            authorities = authorities
        )

        // HTTP-Only 쿠키로 토큰 전달 (보안 개선)
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

        log.info("JWT tokens set as HTTP-Only cookies for user: ${user.email}")

        // 쿼리 파라미터 없이 리다이렉트
        response.sendRedirect(redirectUri)
    }
}
