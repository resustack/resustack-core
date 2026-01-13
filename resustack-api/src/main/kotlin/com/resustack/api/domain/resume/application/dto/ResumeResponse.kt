package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.model.Section
import com.resustack.api.domain.resume.model.Skills
import java.time.LocalDateTime

data class ResumeResponse(
    val id: String,
    val userId: Long,
    val title: String,
    val templateId: String,
    val profile: Profile,
    val sections: List<Section>,
    val skills: Skills?,
    val status: ResumeStatus,
    val isPublic: Boolean,
    val createdAt: LocalDateTime?,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(resume: Resume): ResumeResponse {
            return ResumeResponse(
                id = resume.id ?: throw IllegalStateException("Resume ID should not be null"),
                userId = resume.userId,
                title = resume.title,
                templateId = resume.templateId,
                profile = resume.profile,
                sections = resume.sections,
                skills = resume.skills,
                status = resume.status,
                isPublic = resume.isPublic,
                createdAt = resume.createdAt,
                updatedAt = resume.updatedAt
            )
        }
    }
}
