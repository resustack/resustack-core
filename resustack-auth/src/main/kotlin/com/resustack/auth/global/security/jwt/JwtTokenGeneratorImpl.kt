package com.resustack.auth.global.security.jwt

import com.resustack.auth.oauth.model.TokenResponse
import com.resustack.common.security.jwt.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtTokenGeneratorImpl(
    private val jwtProperties: JwtProperties
) : JwtTokenGenerator {

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
        log.info("JwtTokenGenerator initialized")
    }

    override fun generateToken(userId: Long, email: String, authorities: Collection<String>): TokenResponse {
        log.info("Generating token for user: userId=$userId, email=$email")

        val now = Instant.now()
        val accessTokenExpiresIn = now.plus(jwtProperties.accessTokenValidityInSeconds, ChronoUnit.SECONDS)
        val refreshTokenExpiresIn = now.plus(jwtProperties.refreshTokenValidityInSeconds, ChronoUnit.SECONDS)

        val authoritiesString = authorities.joinToString(",")

        val accessToken = Jwts.builder()
            .subject(email)
            .claim(USER_ID_KEY, userId)
            .claim(AUTHORITIES_KEY, authoritiesString)
            .issuedAt(Date.from(now))
            .expiration(Date.from(accessTokenExpiresIn))
            .signWith(key)
            .compact()

        val refreshToken = Jwts.builder()
            .subject(email)
            .expiration(Date.from(refreshTokenExpiresIn))
            .signWith(key)
            .compact()

        log.info("Token generated successfully for email: $email")

        return TokenResponse(
            tokenType = "Bearer",
            accessToken = accessToken,
            refreshToken = refreshToken
        )
    }
}
