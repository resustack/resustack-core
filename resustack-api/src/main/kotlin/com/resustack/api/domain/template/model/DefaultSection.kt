package com.resustack.api.domain.template.model

import com.resustack.api.domain.resume.model.Block
import com.resustack.api.domain.resume.model.SectionType

/**
 * 기본 섹션 구조
 */
data class DefaultSection(
    val type: SectionType,
    val title: String,
    val blocks: List<Block> = emptyList()
)
