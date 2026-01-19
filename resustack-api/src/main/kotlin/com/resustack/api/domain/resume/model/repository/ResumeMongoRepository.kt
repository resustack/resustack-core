package com.resustack.api.domain.resume.model.repository

import com.resustack.api.domain.resume.model.ResumeEntity
import com.resustack.api.domain.resume.model.ResumeStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface ResumeMongoRepository : MongoRepository<ResumeEntity, String> {
    fun findAllByUserId(userId: Long): List<ResumeEntity>

    /**
     * 사용자 ID와 상태로 이력서 목록을 페이징 조회
     * @param userId 사용자 ID
     * @param status 이력서 상태
     * @param pageable 페이징 정보 (페이지, 크기, 정렬)
     * @return 페이징된 이력서 엔티티 목록
     */
    fun findAllByUserIdAndStatus(
        userId: Long,
        status: ResumeStatus,
        pageable: Pageable
    ): Page<ResumeEntity>
}
