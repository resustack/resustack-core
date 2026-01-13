package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Block 메타 정보
 */
@Schema(description = "블록 메타 정보 (기술 스택, 링크 등)")
data class BlockMeta(
    @Schema(description = "기술 스택 목록", example = "[\"Spring Boot\", \"Kotlin\"]")
    val techStack: List<String> = emptyList(),

    @Schema(description = "관련 링크", example = "https://project.example.com")
    val link: String? = null
)
