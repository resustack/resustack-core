package com.resustack.api.domain.template.application

import com.resustack.api.common.exception.BusinessException
import com.resustack.api.domain.template.application.dto.TemplateCreateRequest
import com.resustack.api.domain.template.application.dto.TemplateResponse
import com.resustack.api.domain.template.model.TemplateStatus
import com.resustack.api.domain.template.repository.TemplateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TemplateService(
    private val templateRepository: TemplateRepository
) {

    /**
     * 템플릿 생성
     * 중복된 이름의 템플릿이 있는지 검증 후 생성
     */
    @Transactional
    fun createTemplate(request: TemplateCreateRequest): TemplateResponse {
        if (templateRepository.existsByName(request.name)) {
            throw BusinessException("이미 존재하는 템플릿 이름입니다: ${request.name}")
        }

        val domain = request.toDomain()
        val savedDomain = templateRepository.save(domain)

        return TemplateResponse.from(savedDomain)
    }

    @Transactional(readOnly = true)
    fun getTemplateById(id: String): TemplateResponse {
        val domain = templateRepository.findById(id)
        return TemplateResponse.from(domain)
    }

    /**
     * 상태별 템플릿 목록 조회
     */
    @Transactional(readOnly = true)
    fun findAllTemplatesByStatus(status: TemplateStatus): List<TemplateResponse> {
        return templateRepository.findAllByStatus(status)
            .map { TemplateResponse.from(it) }
    }
}
