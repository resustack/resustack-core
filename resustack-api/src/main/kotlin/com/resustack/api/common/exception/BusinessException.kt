package com.resustack.api.common.exception

/**
 * 비즈니스 규칙 위반 시 발생하는 예외
 */
class BusinessException(
    message: String
) : RuntimeException(message)
