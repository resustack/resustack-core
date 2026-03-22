package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.ResumeVersion
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "이력서 버전 요약 응답 (목록 조회용)")
data class ResumeVersionSummaryResponse(
    @Schema(description = "버전 번호", example = "3")
    val version: Int,

    @Schema(description = "이력서 제목", example = "Senior Backend Developer Resume")
    val title: String,

    @Schema(description = "버전 생성 시점", example = "2024-03-25T15:30:00")
    val createdAt: LocalDateTime
) {
    companion object {
        fun from(version: ResumeVersion): ResumeVersionSummaryResponse {
            return ResumeVersionSummaryResponse(
                version = version.version,
                title = version.title,
                createdAt = version.createdAt
            )
        }
    }
}
