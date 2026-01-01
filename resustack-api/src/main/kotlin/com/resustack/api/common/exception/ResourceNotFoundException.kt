package com.resustack.api.common.exception

/**
 * 리소스를 찾을 수 없을 때 발생하는 예외
 */
class ResourceNotFoundException(
    message: String = "요청한 리소스를 찾을 수 없습니다"
) : RuntimeException(message)
