package com.resustack.api.domain.resume.application

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeSummaryResponse
import com.resustack.api.domain.resume.application.dto.ResumeUpdateRequest
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.repository.ResumeRepository
import com.resustack.api.domain.template.repository.TemplateRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.security.access.AccessDeniedException

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val templateRepository: TemplateRepository
) {

    /**
     * 이력서 생성
     */
    @Transactional
    fun create(userId: Long, request: ResumeCreateRequest): ResumeResponse {
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
    fun getById(id: String, userId: Long? = null): ResumeResponse {
        val resume = resumeRepository.findById(id)

        // 비공개 이력서이면서, 작성자가 아닌 경우 접근 차단
        if (!resume.isPublic && resume.userId != userId) {
            throw AccessDeniedException("이력서 조회 권한이 없습니다.")
        }

        return ResumeResponse.from(resume)
    }

    /**
     * 내 이력서 목록 조회 (요약 정보, ACTIVE 상태만)
     */
    @Transactional(readOnly = true)
    fun getAllByUserId(userId: Long): List<ResumeSummaryResponse> {
        return resumeRepository.findAllByUserId(userId)
            .filter { it.status == ResumeStatus.ACTIVE }
            .map { ResumeSummaryResponse.from(it) }
    }

    /**
     * 이력서 수정
     * 본인이 작성한 이력서만 수정 가능
     */
    @Transactional
    fun update(id: String, userId: Long, request: ResumeUpdateRequest): ResumeResponse {
        val resume = resumeRepository.findById(id)

        // 본인의 이력서가 아니면 수정 불가
        if (resume.userId != userId) {
            throw AccessDeniedException("이력서 수정 권한이 없습니다.")
        }

        // 수정된 이력서 저장
        val updatedResume = resume.copy(
            title = request.title,
            profile = request.profile,
            sections = request.sections,
            skills = request.skills,
            isPublic = request.isPublic
        )

        val savedResume = resumeRepository.save(updatedResume)
        return ResumeResponse.from(savedResume)
    }

    /**
     * 이력서 삭제 (soft delete)
     * 본인이 작성한 이력서만 삭제 가능
     * status를 INACTIVE로 변경하여 soft delete 수행
     */
    @Transactional
    fun delete(id: String, userId: Long) {
        val resume = resumeRepository.findById(id)

        // 본인의 이력서가 아니면 삭제 불가
        if (resume.userId != userId) {
            throw AccessDeniedException("이력서 삭제 권한이 없습니다.")
        }

        resumeRepository.delete(id)
    }
}
