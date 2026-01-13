package com.resustack.api.domain.template.presentation.swagger

import com.resustack.api.domain.template.application.dto.TemplateCreateRequest
import com.resustack.api.domain.template.application.dto.TemplateResponse
import com.resustack.common.model.ResponseData
import com.resustack.api.domain.template.model.TemplateStatus
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.http.ResponseEntity

@Tag(name = "Template", description = "템플릿 관리 API")
interface TemplateControllerDocs {

    @Operation(
        summary = "템플릿 생성",
        description = "새로운 템플릿을 생성합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "템플릿 생성 성공"
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 요청"
            )
        ]
    )
    fun createTemplate(
        @Parameter(description = "템플릿 생성 요청 정보", required = true)
        @Valid @RequestBody request: TemplateCreateRequest
    ): ResponseEntity<ResponseData<TemplateResponse>>

    @Operation(
        summary = "템플릿 조회",
        description = "ID로 템플릿을 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "템플릿 조회 성공"
            ),
            ApiResponse(
                responseCode = "404",
                description = "템플릿을 찾을 수 없음"
            )
        ]
    )
    fun getTemplateById(
        @Parameter(description = "템플릿 ID", required = true, example = "507f1f77bcf86cd799439011")
        @PathVariable id: String
    ): ResponseEntity<ResponseData<TemplateResponse>>

    @Operation(
        summary = "상태별 템플릿 목록 조회",
        description = "특정 상태의 템플릿 목록을 조회합니다."
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "템플릿 목록 조회 성공"
            ),
            ApiResponse(
                responseCode = "400",
                description = "잘못된 상태값"
            )
        ]
    )
    fun getTemplatesByStatus(
        @Parameter(description = "템플릿 상태", required = true, example = "ACTIVE")
        @RequestParam status: TemplateStatus
    ): ResponseEntity<ResponseData<List<TemplateResponse>>>
}
