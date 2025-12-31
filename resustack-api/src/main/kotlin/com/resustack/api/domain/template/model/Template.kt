package com.resustack.api.domain.template.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Aggregate Root - Template
 * 템플릿 도메인의 최상위 집합체
 */
@Document(collection = "templates")
data class Template(
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
)
