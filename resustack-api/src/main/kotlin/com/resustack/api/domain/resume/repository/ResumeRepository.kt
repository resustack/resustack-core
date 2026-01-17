package com.resustack.api.domain.resume.repository

import com.resustack.api.domain.resume.model.Resume

interface ResumeRepository {

    fun save(resume: Resume): Resume

    fun findById(id: String): Resume

    fun findAllByUserId(userId: Long): List<Resume>

    /**
     * soft delete: status를 INACTIVE로 변경
     */
    fun delete(id: String): Resume
}
