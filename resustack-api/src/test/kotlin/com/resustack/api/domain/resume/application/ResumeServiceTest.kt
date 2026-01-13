package com.resustack.api.domain.resume.application

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.repository.ResumeRepository
import com.resustack.api.domain.template.repository.TemplateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import org.mockito.kotlin.verify
import java.util.UUID

@ExtendWith(MockitoExtension::class)
class ResumeServiceTest {

    @Mock
    lateinit var resumeRepository: ResumeRepository

    @Mock
    lateinit var templateRepository: TemplateRepository

    @InjectMocks
    lateinit var resumeService: ResumeService

    @Nested
    inner class CreateResume {
        @Test
        fun `성공`() {
            // given
            val userId = 1L
            val templateId = "template-id-1"
            val request = ResumeCreateRequest(
                title = "Test Resume",
                templateId = templateId,
                profile = Profile(name = "User")
            )

            whenever(templateRepository.existsById(templateId)).thenReturn(true)
            whenever(resumeRepository.save(any())).thenAnswer { 
                (it.arguments[0] as Resume).copy(id = "resume-1") 
            }

            // when
            val response = resumeService.createResume(userId, request)

            // then
            assertNotNull(response)
            assertEquals(request.title, response.title)
            assertEquals(request.templateId, response.templateId)
            verify(templateRepository).existsById(templateId)
            verify(resumeRepository).save(any())
        }

        @Test
        fun `템플릿 없음`() {
            // given
            val userId = 1L
            val templateId = "non-existent-template"
            val request = ResumeCreateRequest(
                title = "Test Resume",
                templateId = templateId,
                profile = Profile(name = "User")
            )

            whenever(templateRepository.existsById(templateId)).thenReturn(false)

            // when & then
            assertThrows(ResourceNotFoundException::class.java) {
                resumeService.createResume(userId, request)
            }
        }
    }

    @Nested
    inner class GetResumeById {
        @Test
        fun `성공`() {
            // given
            val resumeId = UUID.randomUUID().toString()
            val resume = Resume(
                id = resumeId,
                userId = 1L,
                title = "My Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                status = ResumeStatus.ACTIVE
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)

            // when
            val result = resumeService.getResumeById(resumeId)

            // then
            assertEquals(resumeId, result.id)
            assertEquals(resume.title, result.title)
        }
    }

    @Nested
    inner class GetResumesByUserId {
        @Test
        fun `성공`() {
            // given
            val userId = 1L
            val resumes = listOf(
                Resume(
                    id = "1",
                    userId = userId,
                    title = "Resume 1",
                    templateId = "t1",
                    profile = Profile(name = "User"),
                    status = ResumeStatus.ACTIVE
                ),
                Resume(
                    id = "2",
                    userId = userId,
                    title = "Resume 2",
                    templateId = "t2",
                    profile = Profile(name = "User"),
                    status = ResumeStatus.ACTIVE
                )
            )

            whenever(resumeRepository.findAllByUserId(userId)).thenReturn(resumes)

            // when
            val results = resumeService.getResumesByUserId(userId)

            // then
            assertEquals(2, results.size)
            assertEquals("Resume 1", results[0].title)
            assertEquals("Resume 2", results[1].title)
        }
    }
}
