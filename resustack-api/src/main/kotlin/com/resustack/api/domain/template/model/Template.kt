package com.resustack.api.domain.template.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

/**
 * Aggregate Root - Template
 * 템플릿 도메인의 최상위 집합체
 */
@Document(collection = "templates")
data class Template(
    @Id
    val id: String,

    val name: String,

    val description: String? = null,

    val thumbnail: String? = null,

    val layoutType: LayoutType,

    val theme: Theme,

    val defaultSections: List<DefaultSection> = emptyList(),

    val status: TemplateStatus = TemplateStatus.ACTIVE
)
