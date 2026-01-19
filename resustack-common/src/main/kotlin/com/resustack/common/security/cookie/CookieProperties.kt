package com.resustack.common.security.cookie

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 쿠키 설정을 관리하는 Properties 클래스
 *
 * @property domain 쿠키 도메인 (비어있으면 현재 도메인 사용)
 * @property secure HTTPS에서만 전송 여부 (로컬: false, 프로덕션: true)
 * @property sameSite CSRF 방어를 위한 SameSite 속성 (Lax or Strict)
 */
@ConfigurationProperties(prefix = "app.cookie")
data class CookieProperties(
    val domain: String? = null,
    val secure: Boolean = true,
    val sameSite: String = "Lax"
)
