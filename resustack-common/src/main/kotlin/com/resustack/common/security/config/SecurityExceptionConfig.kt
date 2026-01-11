package com.resustack.common.security.config

import com.resustack.common.model.ErrorCode
import com.resustack.common.model.ResponseData
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.ExceptionHandlingConfigurer
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper
import java.nio.charset.StandardCharsets

@Component
class SecurityExceptionConfig(
    private val objectMapper: ObjectMapper
) {
    fun configure(config: ExceptionHandlingConfigurer<HttpSecurity>) {
        config
            .authenticationEntryPoint(customAuthenticationEntryPoint())
            .accessDeniedHandler(customAccessDeniedHandler())
    }

    private fun customAuthenticationEntryPoint() = AuthenticationEntryPoint {
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException ->

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val errorResponse = ResponseData.error<Any>(ErrorCode.UNAUTHORIZED)
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }

    private fun customAccessDeniedHandler() = AccessDeniedHandler {
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException ->

        response.status = HttpServletResponse.SC_FORBIDDEN
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = StandardCharsets.UTF_8.name()

        val errorResponse = ResponseData.error<Any>(ErrorCode.FORBIDDEN)
        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}
