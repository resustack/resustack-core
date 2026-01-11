package com.resustack.auth.oauth.model

class NaverOAuth2UserInfo(
    attributes: Map<String, Any>
) : OAuth2UserInfo(attributes) {

    private fun getResponse(): Map<String, Any> {
        return attributes["response"] as? Map<String, Any> ?: emptyMap()
    }

    override fun getEmail(): String {
        return getResponse()["email"] as? String ?: ""
    }

    override fun getName(): String? {
        return getResponse()["name"] as? String
    }

    override fun getProfileImageUrl(): String? {
        return getResponse()["profile_image"] as? String
    }

    override fun getGender(): String? {
        return getResponse()["gender"] as? String
    }

    override fun getBirthYear(): String? {
        return getResponse()["birthyear"] as? String
    }
}
