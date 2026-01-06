package com.resustack.api.domain.template.presentation.template

import com.resustack.api.domain.template.application.dto.TemplateCreateRequest
import com.resustack.api.domain.template.application.dto.TemplateResponse
import com.resustack.api.domain.template.application.TemplateService
import com.resustack.common.model.ResponseData
import com.resustack.api.domain.template.model.TemplateStatus
import com.resustack.api.domain.template.presentation.template.swagger.TemplateControllerDocs
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/templates")
class TemplateController(
    private val templateService: TemplateService
) : TemplateControllerDocs {

    /**
     * 템플릿 생성 API
     */
    @PostMapping(version = "1.0")
    override fun createTemplate(
        @Valid @RequestBody request: TemplateCreateRequest
    ): ResponseEntity<ResponseData<TemplateResponse>> {
        val response = templateService.createTemplate(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.of(HttpStatus.CREATED, response))
    }

    /**
     * 템플릿 단건 조회 API (ID)
     */
    @GetMapping("/{id}", version = "1.0")
    override fun getTemplateById(
        @PathVariable id: String
    ): ResponseEntity<ResponseData<TemplateResponse>> {
        val response = templateService.getTemplateById(id)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, response))
    }

    /**
     * 상태별 템플릿 목록 조회 API
     */
    @GetMapping(version = "1.0")
    override fun getTemplatesByStatus(
        @RequestParam status: TemplateStatus
    ): ResponseEntity<ResponseData<List<TemplateResponse>>> {
        val responses = templateService.findAllTemplatesByStatus(status)
        return ResponseEntity.ok(ResponseData.of(HttpStatus.OK, responses))
    }
}
