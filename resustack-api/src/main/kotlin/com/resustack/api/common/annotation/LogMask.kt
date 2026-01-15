package com.resustack.api.common.annotation

/**
 * 로그 출력 시 민감한 정보를 마스킹하기 위한 어노테이션
 *
 * 컨트롤러 메서드의 파라미터에 이 어노테이션을 붙이면
 * AOP 로깅 시 값이 [PROTECTED]로 마스킹처리 됩니다.
 */
@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
annotation class LogMask
