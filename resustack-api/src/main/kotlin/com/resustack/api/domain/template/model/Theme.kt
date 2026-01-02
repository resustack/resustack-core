package com.resustack.api.domain.template.model

import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank

/**
 * Theme 정보
 */
data class Theme(
    @field:NotBlank(message = "Primary 색상은 필수입니다")
    val primaryColor: String,

    @field:NotBlank(message = "Secondary 색상은 필수입니다")
    val secondaryColor: String,

    val fontFamily: String = DEFAULT_FONT_FAMILY,

    @field:Valid
    val spacing: Spacing = Spacing()
) {
    companion object {
        private const val DEFAULT_FONT_FAMILY = "Pretendard"
    }
}
