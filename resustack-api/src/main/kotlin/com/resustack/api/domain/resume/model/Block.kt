package com.resustack.api.domain.resume.model

import java.util.UUID

/**
 * Section 내 개별 항목
 */
data class Block(
    val id: String = UUID.randomUUID().toString(),
    val subTitle: String? = null,
    val period: String? = null,
    val content: String? = null,
    val isVisible: Boolean = true,
    val blockMeta: BlockMeta? = null
)
