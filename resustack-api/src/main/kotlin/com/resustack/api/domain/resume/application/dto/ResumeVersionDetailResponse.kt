package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.ResumeVersion
import com.resustack.api.domain.resume.model.Section
import com.resustack.api.domain.resume.model.Skills
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "이력서 버전 상세 응답")
data class ResumeVersionDetailResponse(
    @Schema(description = "이력서 ID", example = "550e8400-e29b-41d4-a716-446655440004")
    val resumeId: String,

    @Schema(description = "버전 번호", example = "3")
    val version: Int,

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

    @Schema(description = "공개 여부", example = "false")
    val isPublic: Boolean,

    @Schema(description = "버전 생성 시점", example = "2024-03-25T15:30:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(version: ResumeVersion): ResumeVersionDetailResponse {
            return ResumeVersionDetailResponse(
                resumeId = version.resumeId,
                version = version.version,
                title = version.title,
                templateId = version.templateId,
                profile = version.profile,
                sections = version.sections,
                skills = version.skills,
                isPublic = version.isPublic,
                createdAt = version.createdAt
            )
        }
    }
}
