package com.resustack.api.domain.template.application.dto

import com.resustack.api.domain.template.model.DefaultSection
import com.resustack.api.domain.template.model.LayoutType
import com.resustack.api.domain.template.model.Template
import com.resustack.api.domain.template.model.Theme
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

/**
 * Template 생성 요청 DTO
 */
data class TemplateCreateRequest(
    @field:NotBlank(message = "템플릿 이름은 필수입니다")
    val name: String,

    val description: String? = null,

    val thumbnail: String? = null,

    @field:NotNull(message = "레이아웃 타입은 필수입니다")
    val layoutType: LayoutType,

    @field:Valid
    @field:NotNull(message = "테마 정보는 필수입니다")
    val theme: Theme,

    @field:Valid
    val defaultSections: List<DefaultSection> = emptyList()
) {
    fun toDomain(): Template {
        return Template(
            name = name,
            description = description,
            thumbnail = thumbnail,
            layoutType = layoutType,
            theme = theme,
            defaultSections = defaultSections
        )
    }
}



