package com.resustack.api.domain.template.infrastructure

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.template.model.Template
import com.resustack.api.domain.template.model.TemplateStatus
import com.resustack.api.domain.template.model.repository.TemplateMongoRepository
import com.resustack.api.domain.template.repository.TemplateRepository
import org.springframework.stereotype.Repository

@Repository
class TemplateRepositoryImpl(
    private val mongoRepository: TemplateMongoRepository
) : TemplateRepository {

    override fun save(domain: Template): Template {
        val entity = domain.toEntity()
        val savedEntity = mongoRepository.save(entity)
        return savedEntity.toDomain()
    }

    override fun findById(id: String): Template {
        val entity = mongoRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("템플릿을 찾을 수 없습니다. ID: $id") }
        return entity.toDomain()
    }

    override fun findAllByStatus(status: TemplateStatus): List<Template> {
        return mongoRepository.findAllByStatus(status)
            .map { it.toDomain() }
    }

    override fun existsByName(name: String): Boolean {
        return mongoRepository.existsByName(name)
    }
}
