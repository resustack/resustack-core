package com.resustack.auth.oauth.model

abstract class OAuth2UserInfo(
    protected val attributes: Map<String, Any>
) {
    abstract fun getEmail(): String
    abstract fun getName(): String?
    abstract fun getProfileImageUrl(): String?
    abstract fun getGender(): String?
    abstract fun getBirthYear(): String?
}

