package com.resustack.api.domain.resume.application

import com.resustack.api.domain.resume.application.dto.ResumeVersionDetailResponse
import com.resustack.api.domain.resume.application.dto.ResumeVersionSummaryResponse
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeVersion
import com.resustack.api.domain.resume.repository.ResumeRepository
import com.resustack.api.domain.resume.repository.ResumeVersionRepository
import com.resustack.common.model.PaginationRequest
import com.resustack.common.model.PaginationResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class ResumeVersionService(
    private val resumeVersionRepository: ResumeVersionRepository,
    private val resumeRepository: ResumeRepository
) {

    companion object {
        const val MAX_VERSION_COUNT = 50
    }

    /**
     * 이력서의 현재 상태를 버전으로 저장
     * 트랜잭션 내에서 호출되므로 별도 @Transactional 불필요
     */
    fun saveVersion(resume: Resume) {
        val nextVersion = resumeVersionRepository.getNextVersion(resume.id!!)

        val version = ResumeVersion(
            resumeId = resume.id,
            version = nextVersion,
            userId = resume.userId,
            title = resume.title,
            templateId = resume.templateId,
            profile = resume.profile,
            sections = resume.sections,
            skills = resume.skills,
            isPublic = resume.isPublic,
            createdAt = LocalDateTime.now()
        )
        resumeVersionRepository.save(version)

        // 최대 버전 수 초과 시 오래된 버전 정리
        resumeVersionRepository.deleteOldVersions(resume.id, MAX_VERSION_COUNT)
    }

    /**
     * 이력서 버전 이력 목록 조회 (페이징, 최신순)
     */
    fun getVersions(
        resumeId: String,
        userId: Long,
        paginationRequest: PaginationRequest
    ): PaginationResponse<ResumeVersionSummaryResponse> {
        validateOwnership(resumeId, userId)

        val pageable = paginationRequest.toPageable()
        val versionPage = resumeVersionRepository.findAllByResumeId(resumeId, pageable)
        return PaginationResponse.from(versionPage) { ResumeVersionSummaryResponse.from(it) }
    }

    /**
     * 특정 버전 상세 조회
     */
    fun getVersionDetail(
        resumeId: String,
        version: Int,
        userId: Long
    ): ResumeVersionDetailResponse {
        validateOwnership(resumeId, userId)

        val resumeVersion = resumeVersionRepository.findByResumeIdAndVersion(resumeId, version)
        return ResumeVersionDetailResponse.from(resumeVersion)
    }

    /**
     * 복원용 도메인 객체 반환 (내부 호출용)
     */
    fun getVersionDomain(resumeId: String, version: Int): ResumeVersion {
        return resumeVersionRepository.findByResumeIdAndVersion(resumeId, version)
    }

    /**
     * 이력서 소유자 검증
     */
    private fun validateOwnership(resumeId: String, userId: Long) {
        val resume = resumeRepository.findById(resumeId)
        if (resume.userId != userId) {
            throw AccessDeniedException("이력서 버전 조회 권한이 없습니다.")
        }
    }
}
