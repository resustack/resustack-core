package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import java.util.UUID

/**
 * 이력서 섹션 (경력, 프로젝트, 스킬 등)
 */
@Schema(description = "이력서 섹션 정보")
data class Section(
    @Schema(description = "섹션 ID", example = "550e8400-e29b-41d4-a716-446655440001")
    val id: String = UUID.randomUUID().toString(),

    @Schema(description = "섹션 타입 (WORK_EXPERIENCE, PROJECT, EDUCATION, ETC)")
    val type: SectionType,

    @field:NotBlank(message = "섹션 제목은 필수입니다.")
    @Schema(description = "섹션 제목", example = "Work Experience")
    val title: String,

    @Schema(description = "정렬 순서", example = "0")
    val orderIndex: Int,

    @field:Valid
    @Schema(description = "섹션 내 블록 목록")
    val blocks: List<Block> = emptyList()
)
