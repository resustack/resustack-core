package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import java.time.LocalDateTime

data class ResumeSummaryResponse(
    val id: String,
    val title: String,
    val status: ResumeStatus,
    val isPublic: Boolean,
    val updatedAt: LocalDateTime?
) {
    companion object {
        fun from(resume: Resume): ResumeSummaryResponse {
            return ResumeSummaryResponse(
                id = resume.id ?: throw IllegalStateException("Resume ID should not be null"),
                title = resume.title,
                status = resume.status,
                isPublic = resume.isPublic,
                updatedAt = resume.updatedAt
            )
        }
    }
}
