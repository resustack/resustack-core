package com.resustack.api.domain.resume.model

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Resume Entity
 * MongoDB 영속성을 위한 엔티티
 */
@Document(collection = "resumes")
data class ResumeEntity(
    @Id
    val id: String? = null,

    @Indexed
    val userId: Long,

    val title: String,

    val templateId: String,

    val profile: Profile,

    val sections: List<Section> = emptyList(),

    val skills: Skills? = null,

    val status: ResumeStatus = ResumeStatus.ACTIVE,

    val isPublic: Boolean = false,

    @CreatedDate
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    val updatedAt: LocalDateTime? = null
) {
    fun toDomain(): Resume {
        return Resume(
            id = id,
            userId = userId,
            title = title,
            templateId = templateId,
            profile = profile,
            sections = sections,
            skills = skills,
            status = status,
            isPublic = isPublic,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}
