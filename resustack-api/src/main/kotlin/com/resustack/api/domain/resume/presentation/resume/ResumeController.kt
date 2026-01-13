package com.resustack.api.domain.resume.presentation.resume

import com.resustack.api.domain.resume.application.ResumeService
import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeSummaryResponse
import com.resustack.api.domain.resume.presentation.resume.swagger.ResumeControllerDocs
import com.resustack.common.model.ResponseData
import com.resustack.common.security.principal.PrincipalDetails
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/resumes")
class ResumeController(
    private val resumeService: ResumeService
) : ResumeControllerDocs {

    @PostMapping(version = "1.0")
    override fun createResume(
        @AuthenticationPrincipal principal: PrincipalDetails,
        @Valid @RequestBody request: ResumeCreateRequest
    ): ResponseEntity<ResponseData<ResumeResponse>> {
        val userId = requireNotNull(principal.getUser().id)
        val response = resumeService.createResume(userId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(HttpStatus.CREATED, response))
    }

    @GetMapping("/{id}", version = "1.0")
    override fun getResumeById(
        @PathVariable id: String
    ): ResponseEntity<ResponseData<ResumeResponse>> {
        val response = resumeService.getResumeById(id)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    @GetMapping(version = "1.0")
    override fun getMyResumes(
        @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<List<ResumeSummaryResponse>>> {
        val userId = requireNotNull(principal.getUser().id)
        val responses = resumeService.getResumesByUserId(userId)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, responses))
    }
}
