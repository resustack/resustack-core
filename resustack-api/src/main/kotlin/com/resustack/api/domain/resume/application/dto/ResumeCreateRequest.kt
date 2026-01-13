package com.resustack.api.domain.resume.application.dto

import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.model.Section
import com.resustack.api.domain.resume.model.Skills

data class ResumeCreateRequest(
    val title: String,
    val templateId: String,
    val profile: Profile,
    val sections: List<Section> = emptyList(),
    val skills: Skills? = null,
    val isPublic: Boolean = false
) {
    fun toDomain(userId: Long): Resume {
        return Resume(
            userId = userId,
            title = title,
            templateId = templateId,
            profile = profile,
            sections = sections,
            skills = skills,
            status = ResumeStatus.ACTIVE,
            isPublic = isPublic
        )
    }
}
