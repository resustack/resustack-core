package com.resustack.api.domain.resume.presentation

import com.resustack.api.domain.resume.application.ResumeService
import com.resustack.api.domain.resume.application.ResumeVersionService
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeVersionDetailResponse
import com.resustack.api.domain.resume.application.dto.ResumeVersionSummaryResponse
import com.resustack.api.domain.resume.presentation.swagger.ResumeVersionControllerDocs
import com.resustack.common.model.PaginationRequest
import com.resustack.common.model.PaginationResponse
import com.resustack.common.model.ResponseData
import com.resustack.common.security.principal.PrincipalDetails
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/resumes/{resumeId}/versions")
class ResumeVersionController(
    private val resumeService: ResumeService,
    private val resumeVersionService: ResumeVersionService
) : ResumeVersionControllerDocs {

    @GetMapping(version = "1.0")
    override fun getVersions(
        @PathVariable resumeId: String,
        @AuthenticationPrincipal principal: PrincipalDetails,
        @Valid @ModelAttribute paginationRequest: PaginationRequest
    ): ResponseEntity<ResponseData<PaginationResponse<ResumeVersionSummaryResponse>>> {
        val userId = requireNotNull(principal.getUser().id)
        val response = resumeVersionService.getVersions(resumeId, userId, paginationRequest)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @GetMapping("/{version}", version = "1.0")
    override fun getVersionDetail(
        @PathVariable resumeId: String,
        @PathVariable version: Int,
        @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<ResumeVersionDetailResponse>> {
        val userId = requireNotNull(principal.getUser().id)
        val response = resumeVersionService.getVersionDetail(resumeId, version, userId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @PostMapping("/{version}/restore", version = "1.0")
    override fun restoreVersion(
        @PathVariable resumeId: String,
        @PathVariable version: Int,
        @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<ResumeResponse>> {
        val userId = requireNotNull(principal.getUser().id)
        val response = resumeService.restore(resumeId, userId, version)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }
}
