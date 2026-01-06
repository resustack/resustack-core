package com.resustack.auth.global.exception.handler

import com.resustack.auth.global.exception.AuthorizationException
import com.resustack.common.model.ErrorCode
import com.resustack.common.model.ResponseData
import com.resustack.common.util.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.AuthenticationException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.getValue

@RestControllerAdvice
class GlobalExceptionHandler {

    private val log by logger()

    @ExceptionHandler(Exception::class)
    fun handleGenericException(e: Exception): ResponseEntity<ResponseData<Void>> {
        log.error("예상하지 못한 오류가 발생했습니다.", e)
        return buildResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCode.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(AuthorizationException::class)
    fun handleAuthorizationException(e: AuthorizationException): ResponseEntity<ResponseData<Void>> {
        log.error("인가 오류 - {}", e.message)
        return buildResponseEntity(e.errorCode.httpStatus, e.errorCode)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(e: AccessDeniedException): ResponseEntity<ResponseData<Void>> {
        log.error("인가 오류 - {}", e.message)
        return buildResponseEntity(HttpStatus.FORBIDDEN, ErrorCode.FORBIDDEN)
    }

    @ExceptionHandler(AuthenticationException::class)
    fun handleAuthentication(e: AuthenticationException): ResponseEntity<ResponseData<Void>> {
        log.error("인증 오류 - {}", e.message)
        return buildResponseEntity(HttpStatus.UNAUTHORIZED, ErrorCode.UNAUTHORIZED)
    }

    private fun buildResponseEntity(httpStatus: HttpStatus, errorCode: ErrorCode): ResponseEntity<ResponseData<Void>> {
        val responseData = ResponseData.of<Void>(
            httpStatus = httpStatus,
            errorCode = errorCode
        )
        return ResponseEntity.status(httpStatus).body(responseData)
    }
}
