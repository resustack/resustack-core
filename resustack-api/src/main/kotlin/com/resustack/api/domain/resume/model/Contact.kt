package com.resustack.api.domain.resume.model

/**
 * 연락처 정보
 */
data class Contact(
    val phone: String? = null,
    val email: String? = null,
    val github: String? = null,
    val linkedin: String? = null,
    val blog: String? = null
)
