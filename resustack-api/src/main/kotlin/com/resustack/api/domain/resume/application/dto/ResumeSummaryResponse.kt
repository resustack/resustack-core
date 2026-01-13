package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import java.time.LocalDateTime

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이력서 요약 응답 (목록 조회용)")
data class ResumeSummaryResponse(
    @Schema(description = "이력서 ID", example = "550e8400-e29b-41d4-a716-446655440004")
    val id: String,

    @Schema(description = "이력서 제목", example = "Senior Backend Developer Resume")
    val title: String,

    @Schema(description = "상태", example = "ACTIVE")
    val status: ResumeStatus,

    @Schema(description = "공개 여부", example = "false")
    val isPublic: Boolean,

    @Schema(description = "수정 일시", example = "2024-03-25T15:30:00")
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(resume: Resume): ResumeSummaryResponse {
            return ResumeSummaryResponse(
                id = resume.id ?: throw IllegalStateException("Resume ID should not be null"),
                title = resume.title,
                status = resume.status,
                isPublic = resume.isPublic,
                updatedAt = resume.updatedAt
            )
        }
    }
}
