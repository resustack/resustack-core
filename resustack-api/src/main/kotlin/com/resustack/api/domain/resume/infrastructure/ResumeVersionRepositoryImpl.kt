package com.resustack.api.domain.resume.infrastructure

import com.resustack.api.common.exception.ResourceConflictException
import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.model.ResumeVersion
import com.resustack.api.domain.resume.model.repository.ResumeVersionMongoRepository
import com.resustack.api.domain.resume.repository.ResumeVersionRepository
import org.springframework.dao.DuplicateKeyException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ResumeVersionRepositoryImpl(
    private val resumeVersionMongoRepository: ResumeVersionMongoRepository
) : ResumeVersionRepository {

    override fun save(version: ResumeVersion): ResumeVersion {
        try {
            return resumeVersionMongoRepository.save(version.toEntity()).toDomain()
        } catch (e: DuplicateKeyException) {
            // 동시 수정 시 같은 버전 번호가 충돌할 수 있음
            throw ResourceConflictException("이력서 버전 저장 중 충돌이 발생했습니다. 다시 시도해주세요.")
        }
    }

    override fun findAllByResumeId(resumeId: String, pageable: Pageable): Page<ResumeVersion> {
        return resumeVersionMongoRepository.findAllByResumeIdOrderByVersionDesc(resumeId, pageable)
            .map { it.toDomain() }
    }

    override fun findByResumeIdAndVersion(resumeId: String, version: Int): ResumeVersion {
        return resumeVersionMongoRepository.findByResumeIdAndVersion(resumeId, version)?.toDomain()
            ?: throw ResourceNotFoundException("이력서 버전을 찾을 수 없습니다. resumeId: $resumeId, version: $version")
    }

    override fun getNextVersion(resumeId: String): Int {
        val latest = resumeVersionMongoRepository.findTopByResumeIdOrderByVersionDesc(resumeId)
        return (latest?.version ?: 0) + 1
    }

    override fun deleteOldVersions(resumeId: String, keepCount: Int) {
        val totalCount = resumeVersionMongoRepository.countByResumeId(resumeId)
        if (totalCount <= keepCount) return

        // 오래된 버전부터 삭제 대상 조회 (버전 오름차순으로 초과분만큼 가져옴)
        val deleteCount = (totalCount - keepCount).toInt()
        val oldVersions = resumeVersionMongoRepository.findAllByResumeIdOrderByVersionAsc(
            resumeId,
            PageRequest.of(0, deleteCount)
        )

        if (oldVersions.hasContent()) {
            val versionsToDelete = oldVersions.content.map { it.version }
            resumeVersionMongoRepository.deleteByResumeIdAndVersionIn(resumeId, versionsToDelete)
        }
    }
}
