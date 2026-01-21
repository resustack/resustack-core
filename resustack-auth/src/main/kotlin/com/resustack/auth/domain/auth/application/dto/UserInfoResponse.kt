package com.resustack.auth.domain.auth.application.dto

import com.resustack.common.domain.user.User

data class UserInfoResponse(
    val id: Long,
    val email: String,
    val name: String,
    val profileImageUrl: String
) {
    companion object {
        fun from(user: User): UserInfoResponse {
            return UserInfoResponse(
                id = requireNotNull(user.id),
                email = user.email,
                name = user.name,
                profileImageUrl = user.profileImageUrl
            )
        }
    }
}
