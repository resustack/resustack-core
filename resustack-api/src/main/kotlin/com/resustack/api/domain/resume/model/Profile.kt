package com.resustack.api.domain.resume.model

/**
 * Profile 정보
 */
data class Profile(
    val name: String,
    val position: String? = null,
    val introduction: String? = null,
    val contact: Contact? = null,
    val photoUrl: String? = null
)
