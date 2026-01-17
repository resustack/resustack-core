package com.resustack.api.domain.resume.application


import com.resustack.api.common.exception.ResourceNotFoundException
import org.springframework.security.access.AccessDeniedException
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeUpdateRequest
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
            val response = resumeService.create(userId, request)

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
                resumeService.create(userId, request)
            }
        }
    }

    @Nested
    inner class GetResumeById {
        @Test
        fun `공개 이력서 조회 성공`() {
            // given
            val resumeId = UUID.randomUUID().toString()
            val resume = Resume(
                id = resumeId,
                userId = 1L,
                title = "Public Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                status = ResumeStatus.ACTIVE,
                isPublic = true
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)

            // when
            val result = resumeService.getById(resumeId, null)

            // then
            assertEquals(resumeId, result.id)
            assertEquals("Public Resume", result.title)
        }

        @Test
        fun `비공개 이력서 조회 성공 (작성자)`() {
            // given
            val resumeId = UUID.randomUUID().toString()
            val userId = 1L
            val resume = Resume(
                id = resumeId,
                userId = userId,
                title = "Private Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)

            // when
            val result = resumeService.getById(resumeId, userId)

            // then
            assertEquals(resumeId, result.id)
            assertEquals("Private Resume", result.title)
        }

        @Test
        fun `비공개 이력서 조회 실패 (작성자 아님)`() {
            // given
            val resumeId = UUID.randomUUID().toString()
            val ownerId = 1L
            val requesterId = 2L
            val resume = Resume(
                id = resumeId,
                userId = ownerId,
                title = "Private Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)

            // when & then
            assertThrows(AccessDeniedException::class.java) {
                resumeService.getById(resumeId, requesterId)
            }
        }

        @Test
        fun `비공개 이력서 조회 실패 (비로그인)`() {
            // given
            val resumeId = UUID.randomUUID().toString()
            val ownerId = 1L
            val resume = Resume(
                id = resumeId,
                userId = ownerId,
                title = "Private Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)

            // when & then
            assertThrows(AccessDeniedException::class.java) {
                resumeService.getById(resumeId, null)
            }
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
            val results = resumeService.getAllByUserId(userId)

            // then
            assertEquals(2, results.size)
            assertEquals("Resume 1", results[0].title)
            assertEquals("Resume 2", results[1].title)
        }
    }

    @Nested
    inner class UpdateResume {
        @Test
        fun `성공`() {
            // given
            val resumeId = "resume-1"
            val userId = 1L
            val existingResume = Resume(
                id = resumeId,
                userId = userId,
                title = "Old Title",
                templateId = "template-1",
                profile = Profile(name = "Old User"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )
            val request = ResumeUpdateRequest(
                title = "Updated Title",
                profile = Profile(name = "Updated User"),
                sections = emptyList(),
                skills = null,
                isPublic = true
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(existingResume)
            whenever(resumeRepository.save(any())).thenAnswer {
                it.arguments[0] as Resume
            }

            // when
            val response = resumeService.update(resumeId, userId, request)

            // then
            assertNotNull(response)
            assertEquals(request.title, response.title)
            assertEquals(request.profile.name, response.profile.name)
            assertEquals(request.isPublic, response.isPublic)
            verify(resumeRepository).findById(resumeId)
            verify(resumeRepository).save(any())
        }

        @Test
        fun `수정 권한 없음 (다른 사용자)`() {
            // given
            val resumeId = "resume-1"
            val ownerId = 1L
            val requesterId = 2L
            val existingResume = Resume(
                id = resumeId,
                userId = ownerId,
                title = "My Resume",
                templateId = "template-1",
                profile = Profile(name = "Owner"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )
            val request = ResumeUpdateRequest(
                title = "Hacked Title",
                profile = Profile(name = "Hacker"),
                isPublic = true
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(existingResume)

            // when & then
            assertThrows(AccessDeniedException::class.java) {
                resumeService.update(resumeId, requesterId, request)
            }
            verify(resumeRepository).findById(resumeId)
        }
    }

    @Nested
    inner class DeleteResume {
        @Test
        fun `성공`() {
            // given
            val resumeId = "resume-1"
            val userId = 1L
            val existingResume = Resume(
                id = resumeId,
                userId = userId,
                title = "My Resume",
                templateId = "template-1",
                profile = Profile(name = "User"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )
            val deletedResume = existingResume.copy(status = ResumeStatus.INACTIVE)

            whenever(resumeRepository.findById(resumeId)).thenReturn(existingResume)
            whenever(resumeRepository.delete(resumeId)).thenReturn(deletedResume)

            // when
            resumeService.delete(resumeId, userId)

            // then
            verify(resumeRepository).findById(resumeId)
            verify(resumeRepository).delete(resumeId)
        }

        @Test
        fun `삭제 권한 없음 (다른 사용자)`() {
            // given
            val resumeId = "resume-1"
            val ownerId = 1L
            val requesterId = 2L
            val existingResume = Resume(
                id = resumeId,
                userId = ownerId,
                title = "My Resume",
                templateId = "template-1",
                profile = Profile(name = "Owner"),
                status = ResumeStatus.ACTIVE,
                isPublic = false
            )

            whenever(resumeRepository.findById(resumeId)).thenReturn(existingResume)

            // when & then
            assertThrows(AccessDeniedException::class.java) {
                resumeService.delete(resumeId, requesterId)
            }
            verify(resumeRepository).findById(resumeId)
        }
    }
}
