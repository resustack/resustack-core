package com.resustack.api.domain.template.model.repository

import com.resustack.api.domain.template.model.TemplateEntity
import com.resustack.api.domain.template.model.TemplateStatus
import org.springframework.data.mongodb.repository.MongoRepository

interface TemplateMongoRepository : MongoRepository<TemplateEntity, String> {

    fun findAllByStatus(status: TemplateStatus): List<TemplateEntity>

    fun existsByName(name: String): Boolean
}
