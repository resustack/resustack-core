package com.resustack.api.domain.resume.presentation.resume

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.application.ResumeService
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeSummaryResponse
import com.resustack.api.domain.resume.model.Contact
import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.common.domain.user.User
import com.resustack.common.domain.user.UserStatus
import com.resustack.common.security.principal.PrincipalDetails
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ResumeControllerTest {

    @Mock
    lateinit var resumeService: ResumeService

    @InjectMocks
    lateinit var resumeController: ResumeController

    private lateinit var principal: PrincipalDetails
    private val userId = 1L

    @BeforeEach
    fun setup() {
        // PrincipalDetails 수동 생성 (SecurityContextHolder 불필요)
        val user = User(
            id = userId,
            email = "test@example.com",
            name = "Tester",
            profileImageUrl = "https://example.com/profile.jpg",
            gender = "M",
            birthYear = "1990",
            status = UserStatus.ACTIVE
        )
        principal = PrincipalDetails(user, mapOf("id" to userId))
    }

    @Nested
    inner class `이력서 생성` {
        @Test
        fun `성공 - 201 Created 응답`() {
            // Given
            val request = ResumeCreateRequest(
                title = "New Resume",
                templateId = "template-1",
                profile = Profile(
                    name = "Test User",
                    contact = Contact(phone = "010-0000-0000", email = "test@example.com")
                )
            )
            val response = ResumeResponse(
                id = "resume-1",
                userId = userId,
                title = "New Resume",
                templateId = "template-1",
                profile = request.profile,
                sections = emptyList(),
                skills = null,
                status = ResumeStatus.ACTIVE,
                isPublic = false,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            given(resumeService.createResume(eq(userId), any())).willReturn(response)

            // When
            val result = resumeController.createResume(principal, request)

            // Then
            assertEquals(HttpStatus.CREATED, result.statusCode)
            assertNotNull(result.body)
            assertEquals(201, result.body?.httpStatus)
            assertEquals("resume-1", result.body?.data?.id)
            verify(resumeService).createResume(eq(userId), any())
        }
    }

    @Nested
    inner class `이력서 ID로 조회` {
        @Test
        fun `성공 - 200 OK 응답`() {
            // Given
            val resumeId = "resume-1"
            val response = ResumeResponse(
                id = resumeId,
                userId = userId,
                title = "My Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                sections = emptyList(),
                skills = null,
                status = ResumeStatus.ACTIVE,
                isPublic = true,
                createdAt = LocalDateTime.now(),
                updatedAt = LocalDateTime.now()
            )

            given(resumeService.getResumeById(resumeId)).willReturn(response)

            // When
            val result = resumeController.getResumeById(resumeId)

            // Then
            assertEquals(HttpStatus.OK, result.statusCode)
            assertNotNull(result.body)
            assertEquals(200, result.body?.httpStatus)
            assertEquals(resumeId, result.body?.data?.id)
            verify(resumeService).getResumeById(resumeId)
        }

        @Test
        fun `실패 - 존재하지 않는 ID 시 예외 발생`() {
            // Given
            val resumeId = "non-existent"
            given(resumeService.getResumeById(resumeId))
                .willThrow(ResourceNotFoundException("이력서를 찾을 수 없습니다."))

            // When & Then
            assertThrows(ResourceNotFoundException::class.java) {
                resumeController.getResumeById(resumeId)
            }
        }
    }

    @Nested
    inner class `내 이력서 목록 조회` {
        @Test
        fun `성공 - 200 OK 응답`() {
            // Given
            val summaryList = listOf(
                ResumeSummaryResponse(
                    id = "1",
                    title = "Resume 1",
                    status = ResumeStatus.ACTIVE,
                    isPublic = true,
                    updatedAt = LocalDateTime.now()
                )
            )

            given(resumeService.getResumesByUserId(userId)).willReturn(summaryList)

            // When
            val result = resumeController.getMyResumes(principal)

            // Then
            assertEquals(HttpStatus.OK, result.statusCode)
            assertNotNull(result.body)
            assertEquals(200, result.body?.httpStatus)
            assertEquals(1, result.body?.data?.size)
            assertEquals("Resume 1", result.body?.data?.get(0)?.title)
            verify(resumeService).getResumesByUserId(userId)
        }
    }
}
