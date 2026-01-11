package com.resustack.common.security.jwt

import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import javax.crypto.SecretKey

@Component
class JwtTokenProvider(
    private val jwtProperties: JwtProperties
) {
    private val log = LoggerFactory.getLogger(this::class.java)
    private lateinit var key: SecretKey

    companion object {
        private const val AUTHORITIES_KEY = "auth"
        private const val USER_ID_KEY = "uid"
    }

    @PostConstruct
    fun init() {
        val keyBytes = Decoders.BASE64.decode(jwtProperties.secret)
        this.key = Keys.hmacShaKeyFor(keyBytes)
        log.info("JwtTokenProvider initialized")
    }

    fun validateToken(token: String): Boolean {
        return try {
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
            true
        } catch (e: SecurityException) {
            log.warn("Invalid JWT signature: ${e.message}")
            false
        } catch (e: MalformedJwtException) {
            log.warn("Invalid JWT token: ${e.message}")
            false
        } catch (e: ExpiredJwtException) {
            log.warn("Expired JWT token: ${e.message}")
            false
        } catch (e: UnsupportedJwtException) {
            log.warn("Unsupported JWT token: ${e.message}")
            false
        } catch (e: IllegalArgumentException) {
            log.warn("JWT claims string is empty: ${e.message}")
            false
        }
    }

    fun getAuthentication(token: String): Authentication {
        val claims = Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

        val authorities = claims[AUTHORITIES_KEY]?.toString()
            ?.split(",")
            ?.map { SimpleGrantedAuthority(it) }
            ?: emptyList()

        val principal = User(claims.subject, "", authorities)

        return UsernamePasswordAuthenticationToken(principal, token, authorities)
    }

    fun getUserIdFromToken(token: String): Long? {
        return try {
            val claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .payload

            claims[USER_ID_KEY]?.toString()?.toLongOrNull()
        } catch (e: Exception) {
            log.warn("Failed to get user ID from token: ${e.message}")
            null
        }
    }
}
