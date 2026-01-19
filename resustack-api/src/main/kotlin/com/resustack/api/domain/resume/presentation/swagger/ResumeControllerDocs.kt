package com.resustack.api.domain.resume.presentation.swagger

import com.resustack.api.domain.resume.application.dto.ResumeCreateRequest
import com.resustack.api.domain.resume.application.dto.ResumeResponse
import com.resustack.api.domain.resume.application.dto.ResumeSummaryResponse
import com.resustack.api.domain.resume.application.dto.ResumeUpdateRequest
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
import org.springframework.web.bind.annotation.RequestBody

@Tag(name = "Resume", description = "이력서 관리 API")
interface ResumeControllerDocs {

    @Operation(summary = "이력서 생성", description = "새로운 이력서를 생성합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "201", description = "이력서 생성 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청")
        ]
    )
    fun createResume(
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails,
        @Parameter(description = "이력서 생성 요청 정보", required = true)
        @Valid @RequestBody request: ResumeCreateRequest
    ): ResponseEntity<ResponseData<ResumeResponse>>

    @Operation(summary = "이력서 상세 조회", description = "ID로 이력서를 상세 조회합니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이력서 조회 성공"),
            ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
        ]
    )
    fun getResumeById(
        @Parameter(description = "이력서 ID", required = true)
        @PathVariable id: String,
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails?
    ): ResponseEntity<ResponseData<ResumeResponse>>

    @Operation(
        summary = "내 이력서 목록 조회",
        description = "로그인한 사용자의 이력서 목록을 페이징 조회합니다 (요약 정보, ACTIVE 상태만)."
    )
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이력서 목록 조회 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 페이징 파라미터")
        ]
    )
    fun getMyResumes(
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails,
        @ParameterObject @Valid @ModelAttribute paginationRequest: PaginationRequest
    ): ResponseEntity<ResponseData<PaginationResponse<ResumeSummaryResponse>>>

    @Operation(summary = "이력서 수정", description = "이력서를 수정합니다. 본인이 작성한 이력서만 수정할 수 있습니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이력서 수정 성공"),
            ApiResponse(responseCode = "400", description = "잘못된 요청"),
            ApiResponse(responseCode = "403", description = "수정 권한 없음"),
            ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
        ]
    )
    fun updateResume(
        @Parameter(description = "이력서 ID", required = true)
        @PathVariable id: String,
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails,
        @Parameter(description = "이력서 수정 요청 정보", required = true)
        @Valid @RequestBody request: ResumeUpdateRequest
    ): ResponseEntity<ResponseData<ResumeResponse>>

    @Operation(summary = "이력서 삭제", description = "이력서를 삭제합니다 (Soft Delete). 본인이 작성한 이력서만 삭제할 수 있습니다.")
    @ApiResponses(
        value = [
            ApiResponse(responseCode = "200", description = "이력서 삭제 성공"),
            ApiResponse(responseCode = "403", description = "삭제 권한 없음"),
            ApiResponse(responseCode = "404", description = "이력서를 찾을 수 없음")
        ]
    )
    fun deleteResume(
        @Parameter(description = "이력서 ID", required = true)
        @PathVariable id: String,
        @Parameter(hidden = true) @AuthenticationPrincipal principal: PrincipalDetails
    ): ResponseEntity<ResponseData<Unit>>
}
