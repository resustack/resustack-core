package com.resustack.api.domain.resume.model.repository

import com.resustack.api.domain.resume.model.ResumeVersionEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository

interface ResumeVersionMongoRepository : MongoRepository<ResumeVersionEntity, String> {

    /**
     * 이력서 ID로 버전 목록을 최신순 페이징 조회
     */
    fun findAllByResumeIdOrderByVersionDesc(
        resumeId: String,
        pageable: Pageable
    ): Page<ResumeVersionEntity>

    /**
     * 이력서 ID와 버전 번호로 특정 버전 조회
     */
    fun findByResumeIdAndVersion(resumeId: String, version: Int): ResumeVersionEntity?

    /**
     * 이력서의 최신 버전 조회 (버전 번호 산출용)
     */
    fun findTopByResumeIdOrderByVersionDesc(resumeId: String): ResumeVersionEntity?

    /**
     * 이력서의 전체 버전 수 조회
     */
    fun countByResumeId(resumeId: String): Long

    /**
     * 오래된 버전 조회 (FIFO 정리용, 버전 오름차순)
     */
    fun findAllByResumeIdOrderByVersionAsc(
        resumeId: String,
        pageable: Pageable
    ): Page<ResumeVersionEntity>

    /**
     * 오래된 버전 일괄 삭제 (FIFO 정리용)
     */
    fun deleteByResumeIdAndVersionIn(resumeId: String, versions: List<Int>)
}
