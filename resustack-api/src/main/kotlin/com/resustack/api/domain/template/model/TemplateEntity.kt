package com.resustack.api.domain.template.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Template Entity
 * MongoDB 영속성을 위한 엔티티
 */
@Document(collection = "templates")
data class TemplateEntity(
    @Id
    val id: String? = null,

    val name: String,

    val description: String? = null,

    val thumbnail: String? = null,

    val layoutType: LayoutType,

    val theme: Theme,

    val defaultSections: List<DefaultSection> = emptyList(),

    val status: TemplateStatus = TemplateStatus.ACTIVE,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
) {
    fun toDomain(): Template {
        return Template(
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

