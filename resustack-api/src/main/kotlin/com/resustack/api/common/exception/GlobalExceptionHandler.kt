package com.resustack.api.common.exception

import com.resustack.common.model.ErrorCode
import com.resustack.common.model.ResponseData
import com.resustack.api.common.util.logger
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import kotlin.getValue

/**
 * 전역 예외 처리 핸들러
 */
@RestControllerAdvice
class GlobalExceptionHandler {

    private val log by logger()

    /**
     * ResourceNotFoundException 처리
     */
    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(
        ex: ResourceNotFoundException
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.of<Nothing>(
            httpStatus = HttpStatus.NOT_FOUND,
            errorCode = ErrorCode.RESOURCE_NOT_FOUND
        )
        val caller = getCallerInfo(ex)
        log.warn("$caller - Resource not found: ${ex.message}")
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response)
    }

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(
        ex: BusinessException
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.of<Nothing>(
            httpStatus = HttpStatus.BAD_REQUEST,
            errorCode = ErrorCode.INVALID_PARAMETER
        )
        val caller = getCallerInfo(ex)
        log.warn("$caller - Business exception: ${ex.message}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(
        ex: MethodArgumentNotValidException
    ): ResponseEntity<ResponseData<Map<String, String>>> {
        val errors = ex.bindingResult.fieldErrors.associate {
            it.field to (it.defaultMessage ?: "유효하지 않은 값입니다")
        }

        val response = ResponseData.of(
            httpStatus = HttpStatus.BAD_REQUEST,
            data = errors,
            errorCode = ErrorCode.INVALID_PARAMETER
        )
        val caller = getCallerInfo(ex)
        log.warn("$caller - Validation failed: $errors")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }



    /**
     * JSON 파싱 오류 처리 (Enum 값 불일치 등)
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(
        ex: HttpMessageNotReadableException
    ): ResponseEntity<ResponseData<Nothing>> {
        val cause = ex.cause

        // Enum 타입 불일치 오류 처리
        if (cause is InvalidFormatException) {
            val targetType = cause.targetType
            if (targetType != null && targetType.isEnum) {
                val allowedValues = targetType.enumConstants.joinToString(", ")
                val invalidValue = cause.value
                val errorMessage = "입력된 값 '${invalidValue}'은(는) 유효하지 않습니다. 허용된 값: [$allowedValues]"
                
                log.warn("Enum validation failed: $errorMessage")
                
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    ResponseData.error(
                        errorCode = ErrorCode.INVALID_PARAMETER
                    )
                )
            }
        }

        val response = ResponseData.error<Nothing>(
            errorCode = ErrorCode.INVALID_PARAMETER
        )

        log.warn("Message not readable: ${ex.message}")
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response)
    }

    /**
     * ResourceConflictException 처리
     */
    @ExceptionHandler(ResourceConflictException::class)
    fun handleResourceConflictException(
        ex: ResourceConflictException
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.of<Nothing>(
            httpStatus = HttpStatus.CONFLICT,
            errorCode = ErrorCode.RESOURCE_CONFLICT
        )
        val caller = getCallerInfo(ex)
        log.warn("$caller - Resource conflict: ${ex.message}")
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response)
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleException(
        ex: Exception
    ): ResponseEntity<ResponseData<Nothing>> {
        val response = ResponseData.of<Nothing>(
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR,
            errorCode = ErrorCode.INTERNAL_SERVER_ERROR
        )
        log.error("Unhandled exception occurred", ex)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response)
    }

    /**
     * 예외가 발생한 클래스와 메서드 정보 추출
     */
    private fun getCallerInfo(ex: Exception): String {
        val stackTrace = ex.stackTrace
        if (stackTrace.isNotEmpty()) {
            val element = stackTrace.first()
            val className = element.className.substringAfterLast('.')
            val methodName = element.methodName
            return "$className.$methodName"
        }
        return "Unknown"
    }
}
