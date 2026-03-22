package com.resustack.api.domain.resume.application

import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import com.resustack.api.domain.resume.model.ResumeVersion
import com.resustack.api.domain.resume.repository.ResumeRepository
import com.resustack.api.domain.resume.repository.ResumeVersionRepository
import com.resustack.common.model.PaginationRequest
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
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.security.access.AccessDeniedException
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class ResumeVersionServiceTest {

    @Mock
    lateinit var resumeVersionRepository: ResumeVersionRepository

    @Mock
    lateinit var resumeRepository: ResumeRepository

    @InjectMocks
    lateinit var resumeVersionService: ResumeVersionService

    private fun createResume(id: String = "resume-1", userId: Long = 1L): Resume {
        return Resume(
            id = id,
            userId = userId,
            title = "Test Resume",
            templateId = "template-1",
            profile = Profile(name = "User"),
            status = ResumeStatus.ACTIVE,
            isPublic = false
        )
    }

    private fun createVersion(
        resumeId: String = "resume-1",
        version: Int = 1,
        userId: Long = 1L
    ): ResumeVersion {
        return ResumeVersion(
            id = "version-$version",
            resumeId = resumeId,
            version = version,
            userId = userId,
            title = "Title v$version",
            templateId = "template-1",
            profile = Profile(name = "User v$version"),
            isPublic = false,
            createdAt = LocalDateTime.now()
        )
    }

    @Nested
    inner class SaveVersion {
        @Test
        fun `성공 - 현재 상태를 다음 버전으로 저장`() {
            // given
            val resume = createResume()
            whenever(resumeVersionRepository.getNextVersion("resume-1")).thenReturn(1)
            whenever(resumeVersionRepository.save(any())).thenAnswer {
                (it.arguments[0] as ResumeVersion).copy(id = "version-1")
            }

            // when
            resumeVersionService.saveVersion(resume)

            // then
            verify(resumeVersionRepository).getNextVersion("resume-1")
            verify(resumeVersionRepository).save(any())
            verify(resumeVersionRepository).deleteOldVersions("resume-1", 50)
        }
    }

    @Nested
    inner class GetVersions {
        @Test
        fun `성공 - 버전 목록 페이징 조회`() {
            // given
            val resumeId = "resume-1"
            val userId = 1L
            val resume = createResume(resumeId, userId)
            val versions = listOf(createVersion(version = 3), createVersion(version = 2), createVersion(version = 1))
            val pageable = PageRequest.of(0, 10)
            val page = PageImpl(versions, pageable, 3)

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)
            whenever(resumeVersionRepository.findAllByResumeId(any(), any())).thenReturn(page)

            val paginationRequest = PaginationRequest(page = 0, size = 10)

            // when
            val result = resumeVersionService.getVersions(resumeId, userId, paginationRequest)

            // then
            assertEquals(3, result.content.size)
            assertEquals(3, result.content[0].version)
        }

        @Test
        fun `실패 - 소유자가 아닌 경우 403`() {
            // given
            val resumeId = "resume-1"
            val ownerId = 1L
            val requesterId = 2L
            val resume = createResume(resumeId, ownerId)

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)

            // when & then
            assertThrows(AccessDeniedException::class.java) {
                resumeVersionService.getVersions(resumeId, requesterId, PaginationRequest())
            }
        }
    }

    @Nested
    inner class GetVersionDetail {
        @Test
        fun `성공 - 특정 버전 상세 조회`() {
            // given
            val resumeId = "resume-1"
            val userId = 1L
            val resume = createResume(resumeId, userId)
            val version = createVersion(resumeId, 2, userId)

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)
            whenever(resumeVersionRepository.findByResumeIdAndVersion(resumeId, 2)).thenReturn(version)

            // when
            val result = resumeVersionService.getVersionDetail(resumeId, 2, userId)

            // then
            assertNotNull(result)
            assertEquals(2, result.version)
            assertEquals("Title v2", result.title)
        }

        @Test
        fun `실패 - 존재하지 않는 버전`() {
            // given
            val resumeId = "resume-1"
            val userId = 1L
            val resume = createResume(resumeId, userId)

            whenever(resumeRepository.findById(resumeId)).thenReturn(resume)
            whenever(resumeVersionRepository.findByResumeIdAndVersion(resumeId, 99))
                .thenThrow(ResourceNotFoundException("이력서 버전을 찾을 수 없습니다."))

            // when & then
            assertThrows(ResourceNotFoundException::class.java) {
                resumeVersionService.getVersionDetail(resumeId, 99, userId)
            }
        }
    }
}
