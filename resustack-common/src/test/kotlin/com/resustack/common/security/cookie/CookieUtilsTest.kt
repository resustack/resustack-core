package com.resustack.common.security.cookie

import jakarta.servlet.http.Cookie
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class CookieUtilsTest {

    @Nested
    inner class CreateAccessTokenCookie {

        @Test
        fun `HttpOnly 속성을 true로 설정해야 한다`() {
            // given
            val properties = CookieProperties(domain = null, secure = true, sameSite = "Lax")
            val token = "test-access-token-123"
            val maxAge = 3600L

            // when
            val cookie = CookieUtils.createAccessTokenCookie(token, maxAge, properties)

            // then
            assertThat(cookie.isHttpOnly).isTrue()
            assertThat(cookie.name).isEqualTo("accessToken")
            assertThat(cookie.value).isEqualTo(token)
            assertThat(cookie.maxAge.seconds).isEqualTo(maxAge)
            assertThat(cookie.isSecure).isTrue()
            assertThat(cookie.sameSite).isEqualTo("Lax")
            assertThat(cookie.path).isEqualTo("/")
        }

        @Test
        fun `도메인이 설정되어 있으면 도메인을 포함해야 한다`() {
            // given
            val properties = CookieProperties(domain = ".resustack.com", secure = true, sameSite = "Lax")
            val token = "test-token"
            val maxAge = 3600L

            // when
            val cookie = CookieUtils.createAccessTokenCookie(token, maxAge, properties)

            // then
            assertThat(cookie.domain).isEqualTo(".resustack.com")
        }

        @Test
        fun `secure 속성이 false이면 HTTP에서도 전송 가능해야 한다`() {
            // given
            val properties = CookieProperties(domain = null, secure = false, sameSite = "Lax")
            val token = "test-token"
            val maxAge = 3600L

            // when
            val cookie = CookieUtils.createAccessTokenCookie(token, maxAge, properties)

            // then
            assertThat(cookie.isSecure).isFalse()
        }

        @Test
        fun `SameSite 속성을 Strict로 설정할 수 있어야 한다`() {
            // given
            val properties = CookieProperties(domain = null, secure = true, sameSite = "Strict")
            val token = "test-token"
            val maxAge = 3600L

            // when
            val cookie = CookieUtils.createAccessTokenCookie(token, maxAge, properties)

            // then
            assertThat(cookie.sameSite).isEqualTo("Strict")
        }
    }

    @Nested
    inner class CreateRefreshTokenCookie {

        @Test
        fun `HttpOnly 속성을 true로 설정해야 한다`() {
            // given
            val properties = CookieProperties(domain = null, secure = true, sameSite = "Lax")
            val token = "test-refresh-token-456"
            val maxAge = 604800L

            // when
            val cookie = CookieUtils.createRefreshTokenCookie(token, maxAge, properties)

            // then
            assertThat(cookie.isHttpOnly).isTrue()
            assertThat(cookie.name).isEqualTo("refreshToken")
            assertThat(cookie.value).isEqualTo(token)
            assertThat(cookie.maxAge.seconds).isEqualTo(maxAge)
            assertThat(cookie.isSecure).isTrue()
            assertThat(cookie.sameSite).isEqualTo("Lax")
            assertThat(cookie.path).isEqualTo("/")
        }
    }

    @Nested
    inner class GetAccessTokenFromCookies {

        @Test
        fun `쿠키에서 Access Token을 추출할 수 있어야 한다`() {
            // given
            val expectedToken = "valid-access-token"
            val cookies = arrayOf(
                Cookie("accessToken", expectedToken),
                Cookie("refreshToken", "some-refresh-token")
            )

            // when
            val token = CookieUtils.getAccessTokenFromCookies(cookies)

            // then
            assertThat(token).isEqualTo(expectedToken)
        }

        @Test
        fun `쿠키가 없으면 null을 반환해야 한다`() {
            // given
            val cookies = emptyArray<Cookie>()

            // when
            val token = CookieUtils.getAccessTokenFromCookies(cookies)

            // then
            assertThat(token).isNull()
        }

        @Test
        fun `쿠키 배열이 null이면 null을 반환해야 한다`() {
            // when
            val token = CookieUtils.getAccessTokenFromCookies(null)

            // then
            assertThat(token).isNull()
        }

        @Test
        fun `accessToken 쿠키가 없으면 null을 반환해야 한다`() {
            // given
            val cookies = arrayOf(
                Cookie("refreshToken", "some-refresh-token"),
                Cookie("otherCookie", "some-value")
            )

            // when
            val token = CookieUtils.getAccessTokenFromCookies(cookies)

            // then
            assertThat(token).isNull()
        }
    }

    @Nested
    inner class GetRefreshTokenFromCookies {

        @Test
        fun `쿠키에서 Refresh Token을 추출할 수 있어야 한다`() {
            // given
            val expectedToken = "valid-refresh-token"
            val cookies = arrayOf(
                Cookie("accessToken", "some-access-token"),
                Cookie("refreshToken", expectedToken)
            )

            // when
            val token = CookieUtils.getRefreshTokenFromCookies(cookies)

            // then
            assertThat(token).isEqualTo(expectedToken)
        }
    }
}
