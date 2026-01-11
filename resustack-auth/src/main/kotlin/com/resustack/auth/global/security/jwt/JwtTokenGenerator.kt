package com.resustack.auth.global.security.jwt

import com.resustack.auth.oauth.model.TokenResponse

interface JwtTokenGenerator {
    fun generateToken(userId: Long, email: String, authorities: Collection<String>): TokenResponse
}
