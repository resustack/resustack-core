package com.resustack.api.domain.resume.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.config.MongoTestContainerConfig
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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@AutoConfigureMockMvc
class ResumeControllerTest : MongoTestContainerConfig() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var resumeService: ResumeService

    private val objectMapper = ObjectMapper()

    private val userId = 1L

    // PrincipalDetails를 포함한 Authentication 모킹 헬퍼 함수
    private fun createMockAuthentication(): UsernamePasswordAuthenticationToken {
        val user = User(
            id = userId,
            email = "test@example.com",
            name = "Tester",
            profileImageUrl = "https://example.com/profile.jpg",
            gender = "M",
            birthYear = "1990",
            status = UserStatus.ACTIVE
        )
        val principal = PrincipalDetails(user, mapOf("id" to userId))
        return UsernamePasswordAuthenticationToken(
            principal,
            null,
            listOf(SimpleGrantedAuthority("ROLE_USER"))
        )
    }

    @Nested
    inner class `이력서 생성` {
        @Test
        fun `성공 - 201 Created 응답`() {
            // given
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

            given(resumeService.create(eq(userId), any())).willReturn(response)

            // when & then
            mockMvc.perform(
                post("/api/resumes")
                    .with(authentication(createMockAuthentication()))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.data.id").value("resume-1"))
                .andExpect(jsonPath("$.data.title").value("New Resume"))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.templateId").value("template-1"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))
                .andExpect(jsonPath("$.data.isPublic").value(false))

            verify(resumeService).create(eq(userId), any())
        }
    }

    @Nested
    inner class `이력서 ID로 조회` {
        @Test
        fun `성공 - 200 OK 응답`() {
            // given
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

            given(resumeService.getById(eq(resumeId), eq(userId))).willReturn(response)

            // when & then
            mockMvc.perform(
                get("/api/resumes/{id}", resumeId)
                    .with(authentication(createMockAuthentication()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.id").value(resumeId))
                .andExpect(jsonPath("$.data.title").value("My Resume"))
                .andExpect(jsonPath("$.data.userId").value(userId))
                .andExpect(jsonPath("$.data.isPublic").value(true))

            verify(resumeService).getById(eq(resumeId), eq(userId))
        }

        @Test
        fun `실패 - 존재하지 않는 ID 시 예외 발생`() {
            // given
            val resumeId = "non-existent"
            given(resumeService.getById(eq(resumeId), any()))
                .willThrow(ResourceNotFoundException("이력서를 찾을 수 없습니다."))

            // when & then
            mockMvc.perform(
                get("/api/resumes/{id}", resumeId)
                    .with(authentication(createMockAuthentication()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)

            verify(resumeService).getById(eq(resumeId), any())
        }
    }

    @Nested
    inner class `내 이력서 목록 조회` {
        @Test
        fun `성공 - 200 OK 응답`() {
            // given
            val summaryList = listOf(
                ResumeSummaryResponse(
                    id = "1",
                    title = "Resume 1",
                    status = ResumeStatus.ACTIVE,
                    isPublic = true,
                    updatedAt = LocalDateTime.now()
                ),
                ResumeSummaryResponse(
                    id = "2",
                    title = "Resume 2",
                    status = ResumeStatus.INACTIVE,
                    isPublic = false,
                    updatedAt = LocalDateTime.now()
                )
            )

            given(resumeService.getAllByUserId(userId)).willReturn(summaryList)

            // when & then
            mockMvc.perform(
                get("/api/resumes")
                    .with(authentication(createMockAuthentication()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("1"))
                .andExpect(jsonPath("$.data[0].title").value("Resume 1"))
                .andExpect(jsonPath("$.data[0].status").value("ACTIVE"))
                .andExpect(jsonPath("$.data[1].id").value("2"))
                .andExpect(jsonPath("$.data[1].title").value("Resume 2"))
                .andExpect(jsonPath("$.data[1].status").value("INACTIVE"))

            verify(resumeService).getAllByUserId(userId)
        }

        @Test
        fun `성공 - 빈 목록 200 OK 응답`() {
            // given
            given(resumeService.getAllByUserId(userId)).willReturn(emptyList())

            // when & then
            mockMvc.perform(
                get("/api/resumes")
                    .with(authentication(createMockAuthentication()))
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.length()").value(0))

            verify(resumeService).getAllByUserId(userId)
        }
    }
}