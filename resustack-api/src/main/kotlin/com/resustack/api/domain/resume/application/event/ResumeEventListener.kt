package com.resustack.api.domain.resume.application.event

import com.resustack.api.domain.resume.application.ResumeVersionService
import com.resustack.common.util.logger
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class ResumeEventListener(
    private val resumeVersionService: ResumeVersionService
) {
    private val log by logger()

    /**
     * 이력서 수정/복원 트랜잭션 커밋 후 버전 저장
     * 버전 저장은 부수 효과이므로 실패해도 이력서 수정 결과에 영향을 주지 않음
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    fun handleResumeUpdated(event: ResumeUpdatedEvent) {
        try {
            resumeVersionService.saveVersion(event.previousSnapshot)
        } catch (e: Exception) {
            log.warn("이력서 버전 저장 실패. resumeId: ${event.previousSnapshot.id}, 원인: ${e.message}")
        }
    }
}
