package com.resustack.api.domain.template.repository

import com.resustack.api.domain.template.model.Template
import com.resustack.api.domain.template.model.TemplateStatus

interface TemplateRepository {

    fun save(template: Template): Template

    fun findById(id: String): Template

    fun findAllByStatus(status: TemplateStatus): List<Template>

    fun existsByName(name: String): Boolean
}
