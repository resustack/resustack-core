package com.resustack.api.domain.resume.application.event

import com.resustack.api.domain.resume.application.ResumeVersionService
import com.resustack.api.domain.resume.model.Profile
import com.resustack.api.domain.resume.model.Resume
import com.resustack.api.domain.resume.model.ResumeStatus
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@ExtendWith(MockitoExtension::class)
class ResumeEventListenerTest {

    @Mock
    lateinit var resumeVersionService: ResumeVersionService

    @InjectMocks
    lateinit var resumeEventListener: ResumeEventListener

    private val testResume = Resume(
        id = "resume-1",
        userId = 1L,
        title = "Test Resume",
        templateId = "template-1",
        profile = Profile(name = "User"),
        status = ResumeStatus.ACTIVE,
        isPublic = false
    )

    @Test
    fun `이벤트 수신 시 saveVersion 호출`() {
        // given
        val event = ResumeUpdatedEvent(previousSnapshot = testResume)

        // when
        resumeEventListener.handleResumeUpdated(event)

        // then
        verify(resumeVersionService).saveVersion(testResume)
    }

    @Test
    fun `버전 저장 실패 시 예외를 전파하지 않음`() {
        // given
        val event = ResumeUpdatedEvent(previousSnapshot = testResume)
        doThrow(RuntimeException("MongoDB 연결 실패")).whenever(resumeVersionService).saveVersion(any())

        // when & then
        assertDoesNotThrow {
            resumeEventListener.handleResumeUpdated(event)
        }
    }
}
