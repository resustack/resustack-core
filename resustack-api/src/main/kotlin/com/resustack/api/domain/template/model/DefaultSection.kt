package com.resustack.api.domain.template.model

import com.resustack.api.domain.resume.model.SectionType
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * 기본 섹션 구조
 */
data class DefaultSection(
    @field:NotNull(message = "섹션 타입은 필수입니다")
    val type: SectionType,

    @field:NotBlank(message = "섹션 제목은 필수입니다")
    val title: String,

    @field:Valid
    val blocks: List<Block> = emptyList()
)
