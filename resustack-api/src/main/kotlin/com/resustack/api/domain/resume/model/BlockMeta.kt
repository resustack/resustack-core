package com.resustack.api.domain.resume.model

/**
 * Block 메타 정보
 */
data class BlockMeta(
    val techStack: List<String> = emptyList(),
    val link: String? = null
)
