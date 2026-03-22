package com.resustack.api.domain.resume.application

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeSummaryResponse
import com.resustack.api.domain.resume.application.dto.ResumeUpdateRequest
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.application.event.ResumeUpdatedEvent
import com.resustack.api.domain.resume.repository.ResumeRepository
import com.resustack.api.domain.template.repository.TemplateRepository
import com.resustack.common.model.PaginationRequest
import com.resustack.common.model.PaginationResponse
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Service
import org.springframework.security.access.AccessDeniedException
import org.springframework.transaction.annotation.Transactional

@Service
class ResumeService(
    private val resumeRepository: ResumeRepository,
    private val templateRepository: TemplateRepository,
    private val resumeVersionService: ResumeVersionService,
    private val eventPublisher: ApplicationEventPublisher
) {

    /**
     * 이력서 생성
     */
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
    fun getById(id: String, userId: Long? = null): ResumeResponse {
        val resume = resumeRepository.findById(id)

        // 비공개 이력서이면서, 작성자가 아닌 경우 접근 차단
        if (!resume.isPublic && resume.userId != userId) {
            throw AccessDeniedException("이력서 조회 권한이 없습니다.")
        }

        return ResumeResponse.from(resume)
    }

    /**
     * 내 이력서 목록 조회 (요약 정보, ACTIVE 상태만, 페이징 지원)
     * DB 레벨에서 필터링 및 페이징 처리하여 성능 최적화
     */
    fun getAllByUserId(userId: Long, paginationRequest: PaginationRequest): PaginationResponse<ResumeSummaryResponse> {
        val pageable = paginationRequest.toPageable()
        val resumePage = resumeRepository.findAllByUserIdAndStatus(userId, ResumeStatus.ACTIVE, pageable)
        return PaginationResponse.from(resumePage) { ResumeSummaryResponse.from(it) }
    }

    /**
     * 이력서 수정
     * 본인이 작성한 이력서만 수정 가능
     * 트랜잭션 커밋 후 이벤트로 버전 저장 (관심사 분리)
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

        // 트랜잭션 커밋 후 버전 저장 이벤트 발행
        eventPublisher.publishEvent(ResumeUpdatedEvent(previousSnapshot = resume))

        return ResumeResponse.from(savedResume)
    }

    /**
     * 이력서 버전 복원
     * 복원 전 현재 상태를 이벤트로 버전 보관 (되돌리기의 되돌리기 가능)
     */
    @Transactional
    fun restore(id: String, userId: Long, version: Int): ResumeResponse {
        val resume = resumeRepository.findById(id)

        if (resume.userId != userId) {
            throw AccessDeniedException("이력서 복원 권한이 없습니다.")
        }

        // 대상 버전의 내용으로 이력서 덮어쓰기
        val targetVersion = resumeVersionService.getVersionDomain(id, version)
        val restoredResume = resume.copy(
            title = targetVersion.title,
            profile = targetVersion.profile,
            sections = targetVersion.sections,
            skills = targetVersion.skills,
            isPublic = targetVersion.isPublic
        )

        val savedResume = resumeRepository.save(restoredResume)

        // 트랜잭션 커밋 후 버전 저장 이벤트 발행
        eventPublisher.publishEvent(ResumeUpdatedEvent(previousSnapshot = resume))

        return ResumeResponse.from(savedResume)
    }

    /**
     * 이력서 삭제 (soft delete)
     * 본인이 작성한 이력서만 삭제 가능
     * status를 INACTIVE로 변경하여 soft delete 수행
     */
    fun delete(id: String, userId: Long) {
        val resume = resumeRepository.findById(id)

        // 본인의 이력서가 아니면 삭제 불가
        if (resume.userId != userId) {
            throw AccessDeniedException("이력서 삭제 권한이 없습니다.")
        }

        resumeRepository.delete(id)
    }
}
