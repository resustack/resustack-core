package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.model.Section
import com.resustack.api.domain.resume.model.Skills

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이력서 생성 요청")
data class ResumeCreateRequest(
    @Schema(description = "이력서 제목", example = "Senior Backend Developer Resume")
    val title: String,

    @Schema(description = "템플릿 ID", example = "550e8400-e29b-41d4-a716-446655440003")
    val templateId: String,

    @Schema(description = "프로필 정보")
    val profile: Profile,

    @Schema(description = "섹션 목록")
    val sections: List<Section> = emptyList(),

    @Schema(description = "기술 스택 정보")
    val skills: Skills? = null,

    @Schema(description = "공개 여부", example = "false")
    val isPublic: Boolean = false
) {
    fun toDomain(userId: Long): Resume {
        return Resume(
            userId = userId,
            title = title,
            templateId = templateId,
            profile = profile,
            sections = sections,
            skills = skills,
            status = ResumeStatus.ACTIVE,
            isPublic = isPublic
        )
    }
}
