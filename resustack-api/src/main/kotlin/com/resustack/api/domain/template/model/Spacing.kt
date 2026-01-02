package com.resustack.api.domain.template.model

/**
 * Spacing 정보
 */
data class Spacing(
    val base: String = DEFAULT_SPACING
) {
    companion object {
        private const val DEFAULT_SPACING = "16px"
    }
}
