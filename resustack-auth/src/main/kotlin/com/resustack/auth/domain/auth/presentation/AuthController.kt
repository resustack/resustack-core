package com.resustack.auth.domain.auth.presentation

import com.resustack.auth.domain.auth.application.AuthService
import com.resustack.auth.domain.auth.application.dto.UserInfoResponse
import com.resustack.common.model.ResponseData
import com.resustack.common.security.principal.PrincipalDetails
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val authService: AuthService
) {

    @GetMapping("/me")
    fun getCurrentUser(
        @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<UserInfoResponse>> {
        val userId = requireNotNull(principal.getUser().id)
        val userInfo = authService.getUserInfo(userId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, userInfo))
    }

    @PostMapping("/refresh")
    fun refresh(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<ResponseData<Unit>> {
        authService.refresh(request, response)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK))
    }

    @PostMapping("/logout")
    fun logout(
        @AuthenticationPrincipal principal: PrincipalDetails,
        response: HttpServletResponse
    ): ResponseEntity<ResponseData<Unit>> {
        val userId = requireNotNull(principal.getUser().id)
        authService.logout(userId, response)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK))
    }
}
