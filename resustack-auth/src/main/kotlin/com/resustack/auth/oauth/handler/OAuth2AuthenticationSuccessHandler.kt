package com.resustack.auth.oauth.handler

import com.resustack.auth.global.security.jwt.JwtTokenGenerator
import com.resustack.common.security.principal.PrincipalDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder

@Component
class OAuth2AuthenticationSuccessHandler(
    private val jwtTokenGenerator: JwtTokenGenerator
) : AuthenticationSuccessHandler {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Value("\${app.oauth2.redirect-uri:http://localhost:3000/auth/callback}")
    private lateinit var redirectUri: String

    companion object {
        private const val ACCESS_TOKEN = "accessToken"
        private const val REFRESH_TOKEN = "refreshToken"
    }

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

        val targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
            .queryParam(ACCESS_TOKEN, tokenResponse.accessToken)
            .queryParam(REFRESH_TOKEN, tokenResponse.refreshToken)
            .build()
            .toUriString()

        log.info("Redirecting to: $targetUrl")
        response.sendRedirect(targetUrl)
    }
}

