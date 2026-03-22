package com.resustack.api.domain.resume.repository

import com.resustack.api.domain.resume.model.ResumeVersion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ResumeVersionRepository {

    fun save(version: ResumeVersion): ResumeVersion

    fun findAllByResumeId(resumeId: String, pageable: Pageable): Page<ResumeVersion>

    fun findByResumeIdAndVersion(resumeId: String, version: Int): ResumeVersion

    /**
     * 다음 버전 번호 반환 (최대 버전 + 1, 없으면 1)
     */
    fun getNextVersion(resumeId: String): Int

    /**
     * keepCount를 초과하는 오래된 버전 삭제
     */
    fun deleteOldVersions(resumeId: String, keepCount: Int)
}
