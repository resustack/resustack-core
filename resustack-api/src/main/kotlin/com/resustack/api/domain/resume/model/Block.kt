package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import java.util.UUID

/**
 * Section 내 개별 항목
 */
@Schema(description = "섹션 내 개별 블록 정보 (경력 항목, 프로젝트 등)")
data class Block(
    @Schema(description = "블록 ID", example = "550e8400-e29b-41d4-a716-446655440000")
    val id: String = UUID.randomUUID().toString(),

    @Schema(description = "소제목 (직책, 프로젝트명 등)", example = "Backend Developer")
    val subTitle: String? = null,

    @Schema(description = "기간", example = "2022.03 - 2024.03")
    val period: String? = null,

    @Schema(description = "내용 (설명)", example = "Designed and implemented RESTful APIs using Spring Boot and Kotlin.")
    val content: String? = null,

    @Schema(description = "노출 여부", example = "true")
    val isVisible: Boolean = true,

    @field:Valid
    @Schema(description = "블록 메타 정보")
    val blockMeta: BlockMeta? = null
)
