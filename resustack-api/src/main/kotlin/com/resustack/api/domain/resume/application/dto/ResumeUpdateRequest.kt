package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Section
import com.resustack.api.domain.resume.model.Skills

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

@Schema(description = "이력서 수정 요청")
data class ResumeUpdateRequest(
    @field:NotBlank(message = "이력서 제목은 필수입니다.")
    @Schema(description = "이력서 제목", example = "Senior Backend Developer Resume")
    val title: String,

    @field:Valid
    @Schema(description = "프로필 정보")
    val profile: Profile,

    @field:Valid
    @Schema(description = "섹션 목록")
    val sections: List<Section> = emptyList(),

    @field:Valid
    @Schema(description = "기술 스택 정보")
    val skills: Skills? = null,

    @Schema(description = "공개 여부", example = "false")
    val isPublic: Boolean = false
)
