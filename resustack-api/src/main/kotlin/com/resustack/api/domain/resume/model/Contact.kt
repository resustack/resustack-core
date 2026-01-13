package com.resustack.api.domain.resume.model

/**
 * 연락처 정보
 */
data class Contact(
    val phone: String,
    val email: String,
    val github: String? = null,
    val linkedin: String? = null,
    val blog: String? = null
)
