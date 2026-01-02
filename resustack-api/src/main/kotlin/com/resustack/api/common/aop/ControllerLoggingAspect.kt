package com.resustack.api.common.aop

import com.resustack.api.common.util.logger
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*
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

    companion object {
        private const val HTTP_METHOD_GET = "GET"
        private const val HTTP_METHOD_POST = "POST"
        private const val HTTP_METHOD_PUT = "PUT"
        private const val HTTP_METHOD_DELETE = "DELETE"
        private const val HTTP_METHOD_PATCH = "PATCH"
        private const val HTTP_METHOD_REQUEST = "REQUEST"
        private const val HTTP_METHOD_UNKNOWN = "UNKNOWN"

        private const val DEFAULT_PATH = "/"

        private const val ANNOTATION_METHOD_VALUE = "value"

        private val MAPPING_ANNOTATIONS = listOf(
            GetMapping::class.java,
            PostMapping::class.java,
            PutMapping::class.java,
            DeleteMapping::class.java,
            PatchMapping::class.java,
            RequestMapping::class.java
        )
    }

    /**
     * 모든 컨트롤러 메서드 실행 시 로깅
     */
    @Around("@within(org.springframework.web.bind.annotation.RestController)")
    fun logControllerExecution(joinPoint: ProceedingJoinPoint): Any? {
        val signature = joinPoint.signature as MethodSignature
        val className = joinPoint.target.javaClass.simpleName
        val methodName = signature.name
        val logPrefix = "$className.$methodName"

        // HTTP 메서드와 경로 추출
        val httpMethod = getHttpMethod(signature)
        val path = getRequestPath(signature)

        // 요청 파라미터 로깅
        val args = joinPoint.args
        val paramNames = signature.parameterNames
        val params = buildParameterLog(paramNames, args)

        log.info("$logPrefix - [$httpMethod $path] Request started $params")

        val startTime = System.currentTimeMillis()

        return try {
            val result = joinPoint.proceed()
            val executionTime = System.currentTimeMillis() - startTime
            log.info("$logPrefix - [$httpMethod $path] Request completed successfully (${executionTime}ms)")
            result
        } catch (ex: Exception) {
            val executionTime = System.currentTimeMillis() - startTime
            log.error("$logPrefix - [$httpMethod $path] Request failed with exception (${executionTime}ms): ${ex.message}")
            throw ex
        }
    }

    /**
     * HTTP 메서드 추출
     */
    private fun getHttpMethod(signature: MethodSignature): String {
        val method = signature.method
        return when {
            method.isAnnotationPresent(GetMapping::class.java) -> HTTP_METHOD_GET
            method.isAnnotationPresent(PostMapping::class.java) -> HTTP_METHOD_POST
            method.isAnnotationPresent(PutMapping::class.java) -> HTTP_METHOD_PUT
            method.isAnnotationPresent(DeleteMapping::class.java) -> HTTP_METHOD_DELETE
            method.isAnnotationPresent(PatchMapping::class.java) -> HTTP_METHOD_PATCH
            method.isAnnotationPresent(RequestMapping::class.java) -> {
                val requestMapping = method.getAnnotation(RequestMapping::class.java)
                requestMapping.method.firstOrNull()?.name ?: HTTP_METHOD_REQUEST
            }
            else -> HTTP_METHOD_UNKNOWN
        }
    }

    /**
     * 요청 경로 추출
     */
    private fun getRequestPath(signature: MethodSignature): String {
        val method = signature.method
        return MAPPING_ANNOTATIONS
            .firstOrNull { method.isAnnotationPresent(it) }
            ?.let { annotationClass ->
                val annotation = method.getAnnotation(annotationClass)
                val paths = extractPaths(annotation)
                paths.firstOrNull()
            } ?: DEFAULT_PATH
    }

    /**
     * 매핑 어노테이션에서 경로 추출
     */
    private fun extractPaths(annotation: Annotation): List<String> {
        val result = annotation.annotationClass.java
            .getMethod(ANNOTATION_METHOD_VALUE)
            .invoke(annotation)

        return (result as? Array<*>)?.filterIsInstance<String>() ?: emptyList()
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
