package com.resustack.api.domain.template.model

import java.time.LocalDateTime

/**
 * Template Domain Model
 * 템플릿 도메인 모델
 */
data class Template(
    val id: String? = null,
    val name: String,
    val description: String? = null,
    val thumbnail: String? = null,
    val layoutType: LayoutType,
    val theme: Theme,
    val defaultSections: List<DefaultSection> = emptyList(),
    val status: TemplateStatus = TemplateStatus.ACTIVE,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    fun toEntity(): TemplateEntity {
        return TemplateEntity(
            id = id,
            name = name,
            description = description,
            thumbnail = thumbnail,
            layoutType = layoutType,
            theme = theme,
            defaultSections = defaultSections,
            status = status,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}


