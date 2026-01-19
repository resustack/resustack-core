package com.resustack.common.security.filter

import com.resustack.common.security.cookie.CookieUtils
import com.resustack.common.security.jwt.JwtTokenProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtTokenProvider: JwtTokenProvider
) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    companion object {
        private const val AUTHORIZATION_HEADER = "Authorization"
        private const val BEARER_PREFIX = "Bearer "
    }

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = resolveToken(request)

        if (token != null && jwtTokenProvider.validateToken(token)) {
            val authentication = jwtTokenProvider.getAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
            log.debug("Set authentication for user: ${authentication.name}")
        }

        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        // 1. Authorization 헤더 확인 (기존 API 클라이언트 호환성 유지)
        val bearerToken = request.getHeader(AUTHORIZATION_HEADER)
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            log.debug("Token resolved from Authorization header")
            return bearerToken.substring(BEARER_PREFIX.length)
        }

        // 2. 쿠키에서 토큰 추출 (OAuth2 로그인 이후 브라우저 요청)
        val cookieToken = CookieUtils.getAccessTokenFromCookies(request.cookies)
        if (cookieToken != null) {
            log.debug("Token resolved from cookie")
            return cookieToken
        }

        return null
    }
}
