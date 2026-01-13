package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 연락처 정보
 */
@Schema(description = "연락처 정보")
data class Contact(
    @Schema(description = "전화번호", example = "010-1234-5678")
    val phone: String,

    @Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @Schema(description = "GitHub 주소", example = "https://github.com/user")
    val github: String? = null,

    @Schema(description = "LinkedIn 주소", example = "https://linkedin.com/in/user")
    val linkedin: String? = null,

    @Schema(description = "블로그 주소", example = "https://blog.example.com")
    val blog: String? = null
)
