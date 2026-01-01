package com.resustack.api.domain.template.application.dto

import com.resustack.api.domain.template.model.DefaultSection
import com.resustack.api.domain.template.model.LayoutType
import com.resustack.api.domain.template.model.Template
import com.resustack.api.domain.template.model.TemplateStatus
import com.resustack.api.domain.template.model.Theme
import java.time.LocalDateTime

data class TemplateResponse(
    val id: String,
    val name: String,
    val description: String?,
    val thumbnail: String?,
    val layoutType: LayoutType,
    val theme: Theme,
    val defaultSections: List<DefaultSection>,
    val status: TemplateStatus,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(template: Template): TemplateResponse {
            return TemplateResponse(
                id = checkNotNull(template.id),
                name = template.name,
                description = template.description,
                thumbnail = template.thumbnail,
                layoutType = template.layoutType,
                theme = template.theme,
                defaultSections = template.defaultSections,
                status = template.status,
                createdAt = template.createdAt,
                updatedAt = template.updatedAt
            )
        }
    }
}
