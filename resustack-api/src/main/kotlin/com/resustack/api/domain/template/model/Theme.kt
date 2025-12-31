package com.resustack.api.domain.template.model

private const val DEFAULT_FONT_FAMILY = "Pretendard"

/**
 * Theme 정보
 */
data class Theme(
    val primaryColor: String,
    val secondaryColor: String,
    val fontFamily: String = DEFAULT_FONT_FAMILY,
    val spacing: Spacing = Spacing()
)
