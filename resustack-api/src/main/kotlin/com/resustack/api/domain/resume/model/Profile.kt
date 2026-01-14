package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

/**
 * Profile 정보
 */
@Schema(description = "프로필 정보")
data class Profile(
    @field:NotBlank(message = "이름은 필수입니다.")
    @Schema(description = "이름", example = "John Doe")
    val name: String,

    @Schema(description = "직군/포지션", example = "Senior Software Engineer")
    val position: String? = null,

    @Schema(description = "자기소개", example = "Passionate developer with 5 years of experience in building scalable web applications.")
    val introduction: String? = null,

    @field:Valid
    @Schema(description = "연락처 정보")
    val contact: Contact? = null,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    val photoUrl: String? = null
)
