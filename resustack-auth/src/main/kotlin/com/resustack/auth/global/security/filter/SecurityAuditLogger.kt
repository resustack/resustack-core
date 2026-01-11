package com.resustack.auth.global.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class SecurityAuditLogger : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        } finally {
            val user = SecurityContextHolder.getContext().authentication?.name ?: "anonymous"

            log.info(
                "Security Audit - User: {}, Path: {}, Method: {}, Status: {}",
                user,
                request.requestURI,
                request.method,
                response.status
            )
        }
    }
}

