package com.resustack.api.domain.resume.model

import java.time.LocalDateTime

/**
 * Resume Version Domain Model
 * 이력서 버전 도메인 모델
 */
data class ResumeVersion(
    val id: String? = null,
    val resumeId: String,
    val version: Int,
    val userId: Long,
    val title: String,
    val templateId: String,
    val profile: Profile,
    val sections: List<Section> = emptyList(),
    val skills: Skills? = null,
    val isPublic: Boolean,
    val createdAt: LocalDateTime
) {
    fun toEntity(): ResumeVersionEntity {
        return ResumeVersionEntity(
            id = id,
            resumeId = resumeId,
            version = version,
            userId = userId,
            title = title,
            templateId = templateId,
            profile = profile,
            sections = sections,
            skills = skills,
            isPublic = isPublic,
            createdAt = createdAt
        )
    }
}
