package com.resustack.api.domain.template.model

data class BlockMeta(
    val techStack: List<String> = emptyList(),
    val link: String? = null
)