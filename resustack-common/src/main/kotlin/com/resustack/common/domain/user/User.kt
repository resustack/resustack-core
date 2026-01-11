package com.resustack.common.domain.user

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "users")
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener::class)
class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "email", nullable = false, unique = true)
    val email: String,

    @Column(name = "name", nullable = false)
    var name: String,

    @Column(name = "profile_image_url", nullable = false)
    var profileImageUrl: String,

    @Column(nullable = false)
    val gender: String,

    @Column(name = "birth_year", nullable = false)
    val birthYear: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: UserStatus = UserStatus.ACTIVE,

    @org.springframework.data.annotation.CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),

    @org.springframework.data.annotation.LastModifiedDate
    @Column(name = "updated_at")
    var updatedAt: LocalDateTime? = null
) {
    companion object {
        fun create(
            email: String,
            name: String,
            profileImageUrl: String,
            gender: String,
            birthYear: String
        ): User {
            return User(
                email = email,
                name = name,
                profileImageUrl = profileImageUrl,
                gender = gender,
                birthYear = birthYear,
                status = UserStatus.ACTIVE
            )
        }
    }
}
