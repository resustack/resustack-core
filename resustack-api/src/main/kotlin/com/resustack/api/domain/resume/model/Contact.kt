package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank

/**
 * 연락처 정보
 */
@Schema(description = "연락처 정보")
data class Contact(
    @field:NotBlank(message = "전화번호는 필수입니다.")
    @Schema(description = "전화번호", example = "010-1234-5678")
    val phone: String,

    @field:NotBlank(message = "이메일은 필수입니다.")
    @field:Email(message = "이메일 형식이 올바르지 않습니다.")
    @Schema(description = "이메일", example = "user@example.com")
    val email: String,

    @Schema(description = "GitHub 주소", example = "https://github.com/user")
    val github: String? = null,

    @Schema(description = "LinkedIn 주소", example = "https://linkedin.com/in/user")
    val linkedin: String? = null,

    @Schema(description = "블로그 주소", example = "https://blog.example.com")
    val blog: String? = null
)
