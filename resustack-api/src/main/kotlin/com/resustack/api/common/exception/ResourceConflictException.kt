package com.resustack.api.common.exception

/**
 * 리소스 중복 시 발생하는 예외
 */
class ResourceConflictException(
    message: String = "리소스가 이미 존재합니다"
) : RuntimeException(message)
