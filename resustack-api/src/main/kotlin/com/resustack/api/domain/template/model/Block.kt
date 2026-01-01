package com.resustack.api.domain.template.model

import jakarta.validation.Valid

data class Block(
    val subTitle: String? = null,
    val period: String? = null,
    val content: String? = null,
    val isVisible: Boolean = true,
    @field:Valid
    val blockMeta: BlockMeta? = null
)
