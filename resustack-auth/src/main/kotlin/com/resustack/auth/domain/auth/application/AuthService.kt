package com.resustack.auth.domain.auth.application

import com.resustack.auth.domain.auth.application.dto.UserInfoResponse
import com.resustack.auth.global.exception.AuthorizationException
import com.resustack.common.domain.user.UserRepository
import com.resustack.common.model.ErrorCode
import com.resustack.common.security.cookie.CookieProperties
import com.resustack.common.security.cookie.CookieUtils
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val cookieProperties: CookieProperties
) {

    @Transactional(readOnly = true)
    fun getUserInfo(userId: Long): UserInfoResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { AuthorizationException(ErrorCode.RESOURCE_NOT_FOUND) }

        return UserInfoResponse.from(user)
    }

    fun logout(response: HttpServletResponse) {
        val accessTokenCookie = CookieUtils.deleteAccessTokenCookie(cookieProperties)
        val refreshTokenCookie = CookieUtils.deleteRefreshTokenCookie(cookieProperties)

        response.addHeader("Set-Cookie", accessTokenCookie.toString())
        response.addHeader("Set-Cookie", refreshTokenCookie.toString())

        SecurityContextHolder.clearContext()
    }
}
