package com.resustack.auth.oauth.model

data class TokenResponse(
    val tokenType: String,
    val accessToken: String,
    val refreshToken: String
)
