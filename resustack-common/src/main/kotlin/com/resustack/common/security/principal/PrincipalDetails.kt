package com.resustack.common.security.principal

import com.resustack.common.domain.user.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.user.OAuth2User

class PrincipalDetails(
    private val user: User,
    private val attributes: Map<String, Any> = emptyMap()
) : OAuth2User {

    fun getUser(): User = user

    fun getUserEmail(): String = user.email

    override fun getName(): String = user.email

    override fun getAttributes(): Map<String, Any> = attributes

    override fun getAuthorities(): Collection<GrantedAuthority> {
        return listOf(SimpleGrantedAuthority("ROLE_USER"))
    }
}
