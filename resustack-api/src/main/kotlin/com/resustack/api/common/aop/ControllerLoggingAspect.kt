package com.resustack.api.common.aop

import com.resustack.api.common.util.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import kotlin.getValue

/**
 * 컨트롤러 요청/응답 로깅을 처리하는 AOP Aspect
 *
 * @RestController가 선언된 클래스의 모든 메서드에 대해
 * 요청 시작, 파라미터, 실행 시간, 응답 상태 로깅
 */
@Aspect
@Component
class ControllerLoggingAspect {

    private val log by logger()

    /**
     * 모든 컨트롤러 메서드 실행 시 로깅
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun logControllerExecution(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val className = joinPoint.target.javaClass.simpleName
        val methodName = signature.name
        val logPrefix = "$className.$methodName"

        val request = (RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes)?.request
        val httpMethod = request?.method ?: "UNKNOWN"
        val requestUri = request?.requestURI ?: "UNKNOWN"

        // 요청 파라미터 로깅
        val args = joinPoint.args
        val paramNames = signature.parameterNames
        val params = buildParameterLog(paramNames, args)

        log.info("$logPrefix - [$httpMethod $requestUri] Request started $params")

        val startTime = System.currentTimeMillis()

        return try {
            val result = joinPoint.proceed()
            val executionTime = System.currentTimeMillis() - startTime
            log.info("$logPrefix - [$httpMethod $requestUri] Request completed successfully (${executionTime}ms)")
            result
        } catch (ex: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            log.error("$logPrefix - [$httpMethod $requestUri] Request failed with exception (${executionTime}ms): ${ex.message}")
            throw ex
        }
    }

    /**
     * 파라미터 로그 생성
     */
    private fun buildParameterLog(paramNames: Array<String>, args: Array<Any?>): String {
        if (paramNames.isEmpty() || args.isEmpty()) {
            return ""
        }

        val params = paramNames.zip(args)
            .filter { (_, value) -> value != null }
            .joinToString(", ") { (name, value) ->
                when (value) {
                    is String -> "$name='$value'"
                    is Enum<*> -> "$name=$value"
                    is Number -> "$name=$value"
                    is Boolean -> "$name=$value"
                    else -> "$name=${value?.javaClass?.simpleName}"
                }
            }

        return if (params.isNotEmpty()) "with [$params]" else ""
    }
}
