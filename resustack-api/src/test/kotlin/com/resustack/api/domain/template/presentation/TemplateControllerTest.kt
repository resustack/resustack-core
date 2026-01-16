package com.resustack.api.domain.template.presentation

import com.fasterxml.jackson.databind.ObjectMapper
import com.resustack.api.common.exception.ResourceConflictException
import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.config.MongoTestContainerConfig
import com.resustack.api.domain.template.application.TemplateService
import com.resustack.api.domain.template.application.dto.TemplateCreateRequest
import com.resustack.api.domain.template.application.dto.TemplateResponse
import com.resustack.api.domain.template.model.LayoutType
import com.resustack.api.domain.template.model.Spacing
import com.resustack.api.domain.template.model.TemplateStatus
import com.resustack.api.domain.template.model.Theme
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import java.time.LocalDateTime

@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
class TemplateControllerTest : MongoTestContainerConfig() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var templateService: TemplateService

    private val objectMapper = ObjectMapper()

    // 공통 테스트 데이터
    private fun createSampleTheme() = Theme(
        primaryColor = "#000000",
        secondaryColor = "#FFFFFF",
        fontFamily = "Pretendard",
        spacing = Spacing(base = "16px")
    )

    private fun createSampleRequest(
        name: String = "Modern Template",
        description: String? = "A modern resume template",
        thumbnail: String? = "https://example.com/thumbnail.png"
    ) = TemplateCreateRequest(
        name = name,
        description = description,
        thumbnail = thumbnail,
        layoutType = LayoutType.SINGLE_COLUMN,
        theme = createSampleTheme(),
        defaultSections = emptyList()
    )

    private fun createSampleResponse(
        id: String = "template-123",
        name: String = "Modern Template",
        description: String? = "A modern resume template",
        layoutType: LayoutType = LayoutType.SINGLE_COLUMN,
        status: TemplateStatus = TemplateStatus.ACTIVE
    ) = TemplateResponse(
        id = id,
        name = name,
        description = description,
        thumbnail = "https://example.com/thumbnail.png",
        layoutType = layoutType,
        theme = createSampleTheme(),
        defaultSections = emptyList(),
        status = status,
        createdAt = LocalDateTime.now(),
        updatedAt = LocalDateTime.now()
    )

    @Nested
    inner class `템플릿 생성` {

        @Test
        fun `성공 - 201 Created 응답`() {
            // given
            val request = createSampleRequest()
            val response = createSampleResponse()
            given(templateService.createTemplate(any())).willReturn(response)

            // when & then
            mockMvc.perform(
                post("/api/templates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isCreated)
                .andExpect(jsonPath("$.httpStatus").value(201))
                .andExpect(jsonPath("$.data.id").value("template-123"))
                .andExpect(jsonPath("$.data.name").value("Modern Template"))
                .andExpect(jsonPath("$.data.description").value("A modern resume template"))
                .andExpect(jsonPath("$.data.layoutType").value("SINGLE_COLUMN"))
                .andExpect(jsonPath("$.data.status").value("ACTIVE"))

            verify(templateService).createTemplate(any())
        }

        @Test
        fun `실패 - 중복된 이름 시 예외 발생`() {
            // given
            val request = createSampleRequest(name = "Duplicate Template")
            given(templateService.createTemplate(any()))
                .willThrow(ResourceConflictException("이미 존재하는 템플릿 이름입니다: Duplicate Template"))

            // when & then
            mockMvc.perform(
                post("/api/templates")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(request))
            )
                .andExpect(status().isConflict)

            verify(templateService).createTemplate(any())
        }
    }

    @Nested
    inner class `템플릿 ID로 조회` {

        @Test
        fun `성공 - 200 OK 응답`() {
            // given
            val templateId = "template-123"
            val response = createSampleResponse(id = templateId)
            given(templateService.getTemplateById(templateId)).willReturn(response)

            // when & then
            mockMvc.perform(
                get("/api/templates/{id}", templateId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.id").value(templateId))
                .andExpect(jsonPath("$.data.name").value("Modern Template"))
                .andExpect(jsonPath("$.data.layoutType").value("SINGLE_COLUMN"))

            verify(templateService).getTemplateById(templateId)
        }

        @Test
        fun `실패 - 존재하지 않는 ID 시 예외 발생`() {
            // given
            val templateId = "non-existent-id"
            given(templateService.getTemplateById(templateId))
                .willThrow(ResourceNotFoundException("템플릿을 찾을 수 없습니다. ID: $templateId"))

            // when & then
            mockMvc.perform(
                get("/api/templates/{id}", templateId)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isNotFound)

            verify(templateService).getTemplateById(templateId)
        }
    }

    @Nested
    inner class `상태별 템플릿 목록 조회` {

        @Test
        fun `성공 - ACTIVE 상태 200 OK 응답`() {
            // given
            val status = TemplateStatus.ACTIVE
            val responses = listOf(
                createSampleResponse(
                    id = "template-1",
                    name = "Template 1",
                    description = "Description 1",
                    layoutType = LayoutType.SINGLE_COLUMN
                ),
                createSampleResponse(
                    id = "template-2",
                    name = "Template 2",
                    description = "Description 2",
                    layoutType = LayoutType.TWO_COLUMN_LEFT
                )
            )
            given(templateService.findAllTemplatesByStatus(status)).willReturn(responses)

            // when & then
            mockMvc.perform(
                get("/api/templates")
                    .param("status", status.name)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].id").value("template-1"))
                .andExpect(jsonPath("$.data[0].name").value("Template 1"))
                .andExpect(jsonPath("$.data[1].id").value("template-2"))
                .andExpect(jsonPath("$.data[1].name").value("Template 2"))

            verify(templateService).findAllTemplatesByStatus(status)
        }

        @Test
        fun `성공 - INACTIVE 상태 빈 목록 200 OK 응답`() {
            // given
            val status = TemplateStatus.INACTIVE
            given(templateService.findAllTemplatesByStatus(status)).willReturn(emptyList())

            // when & then
            mockMvc.perform(
                get("/api/templates")
                    .param("status", status.name)
                    .contentType(MediaType.APPLICATION_JSON)
            )
                .andExpect(status().isOk)
                .andExpect(jsonPath("$.httpStatus").value(200))
                .andExpect(jsonPath("$.data.length()").value(0))

            verify(templateService).findAllTemplatesByStatus(status)
        }
    }
}
