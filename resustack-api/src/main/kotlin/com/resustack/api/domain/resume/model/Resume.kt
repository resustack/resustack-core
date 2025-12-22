package com.resustack.api.domain.resume.model

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime

/**
 * Aggregate Root - Resume
 * 이력서 도메인의 최상위 집합체
 */
@Document(collection = "resumes")
data class Resume(
    @Id
    val id: String? = null,

    @Indexed
    val userId: Long,

    val title: String,

    val templateId: String,

    val status: ResumeStatus = ResumeStatus.ACTIVE,

    val isPublic: Boolean = false,

    val profile: Profile,

    val sections: List<Section> = emptyList(),

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val updatedAt: LocalDateTime = LocalDateTime.now()
)
