package com.resustack.api.domain.resume.repository

import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ResumeRepository {

    fun save(resume: Resume): Resume

    fun findById(id: String): Resume

    /**
     * 사용자 ID와 상태로 이력서 목록을 페이징 조회
     * @param userId 사용자 ID
     * @param status 이력서 상태
     * @param pageable 페이징 정보 (페이지, 크기, 정렬)
     * @return 페이징된 이력서 도메인 객체 목록
     */
    fun findAllByUserIdAndStatus(
        userId: Long,
        status: ResumeStatus,
        pageable: Pageable
    ): Page<Resume>

    /**
     * soft delete: status를 INACTIVE로 변경
     */
    fun delete(id: String): Resume
}
