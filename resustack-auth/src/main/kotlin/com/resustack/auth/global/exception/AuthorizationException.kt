package com.resustack.auth.global.exception

import com.resustack.common.model.ErrorCode

/**
 * 인가 오류 발생 시 던져지는 예외
 */
class AuthorizationException(
    val errorCode: ErrorCode
) : RuntimeException(errorCode.message)
