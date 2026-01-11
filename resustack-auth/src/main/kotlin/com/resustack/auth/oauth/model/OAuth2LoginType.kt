package com.resustack.auth.oauth.model

enum class OAuth2LoginType(val provider: String) {
    NAVER("naver"),
    KAKAO("kakao"),
    GOOGLE("google")
}
