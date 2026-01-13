package com.resustack.api.domain.resume.application

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeSummaryResponse
import com.resustack.api.domain.resume.repository.ResumeRepository
import com.resustack.api.domain.template.repository.TemplateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val templateRepository: TemplateRepository
) {

    /**
     * 이력서 생성
     */
    @Transactional
    fun createResume(userId: Long, request: ResumeCreateRequest): ResumeResponse {
        if (!templateRepository.existsById(request.templateId)) {
            throw ResourceNotFoundException("존재하지 않는 템플릿입니다. ID: ${request.templateId}")
        }

        val resume = request.toDomain(userId)
        val savedResume = resumeRepository.save(resume)
        return ResumeResponse.from(savedResume)
    }

    /**
     * 이력서 상세 조회 (ID)
     */
    @Transactional(readOnly = true)
    fun getResumeById(id: String): ResumeResponse {
        val resume = resumeRepository.findById(id)
        return ResumeResponse.from(resume)
    }

    /**
     * 내 이력서 목록 조회 (요약 정보)
     */
    @Transactional(readOnly = true)
    fun getResumesByUserId(userId: Long): List<ResumeSummaryResponse> {
        return resumeRepository.findAllByUserId(userId)
            .map { ResumeSummaryResponse.from(it) }
    }
}
