package com.resustack.api.domain.resume.application.event

import com.resustack.api.domain.resume.model.Resume

/**
 * 이력서 수정/복원 시 발행되는 도메인 이벤트
 * 수정 전 상태를 스냅샷으로 포함하여 핸들러에서 별도 조회 없이 버전 저장 가능
 */
data class ResumeUpdatedEvent(
    val previousSnapshot: Resume
)
