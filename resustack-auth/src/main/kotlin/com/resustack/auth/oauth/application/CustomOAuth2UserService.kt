package com.resustack.auth.oauth.application

import com.resustack.auth.oauth.model.NaverOAuth2UserInfo
import com.resustack.auth.oauth.model.OAuth2LoginType
import com.resustack.auth.oauth.model.OAuth2UserInfo
import com.resustack.common.domain.user.User
import com.resustack.common.domain.user.UserRepository
import com.resustack.common.security.principal.PrincipalDetails
import org.slf4j.LoggerFactory
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository
) : DefaultOAuth2UserService() {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Transactional
    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {
        val oAuth2User = super.loadUser(userRequest)
        val registrationId = userRequest.clientRegistration.registrationId

        log.info("OAuth2 Login attempt with provider: $registrationId")

        val userInfo = getOAuth2UserInfo(registrationId, oAuth2User.attributes)
        val user = saveOrUpdate(userInfo)

        return PrincipalDetails(user, oAuth2User.attributes)
    }

    private fun getOAuth2UserInfo(registrationId: String, attributes: Map<String, Any>): OAuth2UserInfo {
        return when (registrationId) {
            OAuth2LoginType.NAVER.provider -> NaverOAuth2UserInfo(attributes)
            else -> throw OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다: $registrationId")
        }
    }

    private fun saveOrUpdate(userInfo: OAuth2UserInfo): User {
        return userRepository.findByEmail(userInfo.getEmail())
            .map { user -> updateUser(user, userInfo) }
            .orElseGet {
                userRepository.save(
                    User.create(
                        email = userInfo.getEmail(),
                        name = userInfo.getName() ?: "사용자",
                        profileImageUrl = userInfo.getProfileImageUrl() ?: "",
                        gender = userInfo.getGender() ?: "U",
                        birthYear = userInfo.getBirthYear() ?: "0000"
                    )
                )
            }
    }

    private fun updateUser(user: User, userInfo: OAuth2UserInfo): User {
        val newProfileImageUrl = userInfo.getProfileImageUrl() ?: user.profileImageUrl

        if (user.profileImageUrl != newProfileImageUrl) {
            user.profileImageUrl = newProfileImageUrl
            return userRepository.save(user)
        }
        return user
    }
}
