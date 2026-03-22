package com.resustack.api.domain.resume.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.CompoundIndex
import org.springframework.data.mongodb.core.index.CompoundIndexes
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Resume Version Entity
 * 이력서 수정 전 상태를 스냅샷으로 보관하는 MongoDB 엔티티
 */
@Document(collection = "resume_versions")
@CompoundIndexes(
    CompoundIndex(name = "idx_resumeId_version", def = "{'resumeId': 1, 'version': -1}", unique = true),
    CompoundIndex(name = "idx_resumeId_createdAt", def = "{'resumeId': 1, 'createdAt': 1}")
)
data class ResumeVersionEntity(
    @Id
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
    fun toDomain(): ResumeVersion {
        return ResumeVersion(
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
