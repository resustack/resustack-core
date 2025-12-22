package com.resustack.api.domain.resume.model

import java.util.UUID

/**
 * 이력서 섹션 (경력, 프로젝트, 스킬 등)
 */
data class Section(
    val id: String = UUID.randomUUID().toString(),
    val type: SectionType,
    val title: String,
    val orderIndex: Int,
    val blocks: List<Block> = emptyList()
)
