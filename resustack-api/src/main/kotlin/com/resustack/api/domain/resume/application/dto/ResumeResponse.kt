package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.model.Section
import com.resustack.api.domain.resume.model.Skills
import java.time.LocalDateTime

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이력서 상세 응답 (전체 정보)")
data class ResumeResponse(
    @Schema(description = "이력서 ID", example = "550e8400-e29b-41d4-a716-446655440004")
    val id: String,

    @Schema(description = "작성자 ID", example = "123")
    val userId: Long,

    @Schema(description = "이력서 제목", example = "Senior Backend Developer Resume")
    val title: String,

    @Schema(description = "템플릿 ID", example = "550e8400-e29b-41d4-a716-446655440003")
    val templateId: String,

    @Schema(description = "프로필 정보")
    val profile: Profile,

    @Schema(description = "섹션 목록")
    val sections: List<Section>,

    @Schema(description = "기술 스택 정보")
    val skills: Skills?,

    @Schema(description = "상태", example = "ACTIVE")
    val status: ResumeStatus,

    @Schema(description = "공개 여부", example = "false")
    val isPublic: Boolean,

    @Schema(description = "생성 일시", example = "2024-03-24T10:00:00")
    val createdAt: LocalDateTime?,

    @Schema(description = "수정 일시", example = "2024-03-25T15:30:00")
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(resume: Resume): ResumeResponse {
            return ResumeResponse(
                id = resume.id ?: throw IllegalStateException("Resume ID should not be null"),
                userId = resume.userId,
                title = resume.title,
                templateId = resume.templateId,
                profile = resume.profile,
                sections = resume.sections,
                skills = resume.skills,
                status = resume.status,
                isPublic = resume.isPublic,
                createdAt = resume.createdAt,
                updatedAt = resume.updatedAt
            )
        }
    }
}
