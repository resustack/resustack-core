package com.resustack.api.domain.resume.infrastructure

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.model.repository.ResumeMongoRepository
import com.resustack.api.domain.resume.repository.ResumeRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Repository

@Repository
class ResumeRepositoryImpl(
    private val resumeMongoRepository: ResumeMongoRepository
) : ResumeRepository {

    override fun save(resume: Resume): Resume {
        return resumeMongoRepository.save(resume.toEntity()).toDomain()
    }

    override fun findById(id: String): Resume {
        return resumeMongoRepository.findByIdOrNull(id)?.toDomain()
            ?: throw ResourceNotFoundException("이력서를 찾을 수 없습니다. ID: $id")
    }

    override fun findAllByUserId(userId: Long): List<Resume> {
        return resumeMongoRepository.findAllByUserId(userId)
            .map { it.toDomain() }
    }

    override fun delete(id: String): Resume {
        val resume = resumeMongoRepository.findByIdOrNull(id)?.toDomain()
            ?: throw ResourceNotFoundException("이력서를 찾을 수 없습니다. ID: $id")

        val deletedResume = resume.copy(status = ResumeStatus.INACTIVE)
        return resumeMongoRepository.save(deletedResume.toEntity()).toDomain()
    }
}
