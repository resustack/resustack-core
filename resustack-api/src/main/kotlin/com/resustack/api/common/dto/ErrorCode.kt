package com.resustack.api.common.dto

import org.springframework.http.HttpStatus

enum class ErrorCode(val httpStatus: HttpStatus, val message: String) {

    // API 버전
    UNSUPPORTED_API_VERSION(HttpStatus.BAD_REQUEST, "지원하지 않는 API 버전입니다."),
    VERSION_MISMATCH(HttpStatus.NOT_FOUND, "요청한 엔드포인트는 다른 API 버전을 요구합니다."),

    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류가 발생했습니다."),
    INVALID_PARAMETER(HttpStatus.BAD_REQUEST, "입력값이 유효하지 않습니다."),
    RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "요청한 리소스가 DB에 존재하지 않습니다."),
    RESOURCE_CONFLICT(HttpStatus.CONFLICT, "요청한 리소스가 이미 존재합니다.");
}
