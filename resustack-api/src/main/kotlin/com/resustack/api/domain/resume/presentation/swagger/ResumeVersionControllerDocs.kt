package com.resustack.api.domain.resume.presentation.swagger

import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeVersionDetailResponse
import com.resustack.api.domain.resume.application.dto.ResumeVersionSummaryResponse
import com.resustack.common.model.PaginationRequest
import com.resustack.common.model.PaginationResponse
import com.resustack.common.model.ResponseData
import com.resustack.common.security.principal.PrincipalDetails
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "Resume Version", description = "이력서 버전 관리 API")
interface ResumeVersionControllerDocs {

    @Operation(summary = "버전 이력 목록 조회", description = "이력서의 수정 이력을 최신순으로 페이징 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "버전 목록 조회 성공"),
            ApiResponse(responseCode = "403", description = "조회 권한 없음"),
            ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
        ]
    )
    fun getVersions(
        @Parameter(description = "이력서 ID", required = true)
        @PathVariable resumeId: String,
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails,
        @ParameterObject @Valid @ModelAttribute paginationRequest: PaginationRequest
    ): ResponseEntity<ResponseData<PaginationResponse<ResumeVersionSummaryResponse>>>

    @Operation(summary = "특정 버전 상세 조회", description = "이력서의 특정 버전 스냅샷을 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "버전 상세 조회 성공"),
            ApiResponse(responseCode = "403", description = "조회 권한 없음"),
            ApiResponse(responseCode = "404", description = "버전을 찾을 수 없음")
        ]
    )
    fun getVersionDetail(
        @Parameter(description = "이력서 ID", required = true)
        @PathVariable resumeId: String,
        @Parameter(description = "버전 번호", required = true)
        @PathVariable version: Int,
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<ResumeVersionDetailResponse>>

    @Operation(summary = "특정 버전으로 복원", description = "이력서를 특정 버전의 상태로 복원합니다. 복원 전 현재 상태는 새 버전으로 자동 저장됩니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "버전 복원 성공"),
            ApiResponse(responseCode = "403", description = "복원 권한 없음"),
            ApiResponse(responseCode = "404", description = "버전을 찾을 수 없음")
        ]
    )
    fun restoreVersion(
        @Parameter(description = "이력서 ID", required = true)
        @PathVariable resumeId: String,
        @Parameter(description = "복원할 버전 번호", required = true)
        @PathVariable version: Int,
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<ResumeResponse>>
}
