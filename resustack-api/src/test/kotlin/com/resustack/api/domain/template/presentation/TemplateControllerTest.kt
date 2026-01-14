package com.resustack.api.domain.template.presentation

import com.resustack.api.common.exception.ResourceConflictException
import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.template.application.TemplateService
import com.resustack.api.domain.template.application.dto.TemplateCreateRequest
import com.resustack.api.domain.template.application.dto.TemplateResponse
import com.resustack.api.domain.template.model.LayoutType
import com.resustack.api.domain.template.model.Spacing
import com.resustack.api.domain.template.model.TemplateStatus
import com.resustack.api.domain.template.model.Theme
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.http.HttpStatus
import java.time.LocalDateTime

class TemplateControllerTest {

    private lateinit var templateController: TemplateController
    private lateinit var templateService: TemplateService

    @BeforeEach
    fun setUp() {
        templateService = mock()
        templateController = TemplateController(templateService)
    }

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
            // Given
            val request = createSampleRequest()
            val response = createSampleResponse()
            whenever(templateService.createTemplate(any())).thenReturn(response)

            // When
            val result = templateController.createTemplate(request)

            // Then
            assertEquals(HttpStatus.CREATED, result.statusCode)
            assertNotNull(result.body)
            assertEquals(201, result.body?.httpStatus)
            assertEquals("template-123", result.body?.data?.id)
            assertEquals("Modern Template", result.body?.data?.name)
            assertEquals("A modern resume template", result.body?.data?.description)
            assertEquals(LayoutType.SINGLE_COLUMN, result.body?.data?.layoutType)
            assertEquals(TemplateStatus.ACTIVE, result.body?.data?.status)

            verify(templateService, times(1)).createTemplate(any())
        }

        @Test
        fun `실패 - 중복된 이름 시 예외 발생`() {
            // Given
            val request = createSampleRequest(name = "Duplicate Template")
            whenever(templateService.createTemplate(any()))
                .thenThrow(ResourceConflictException("이미 존재하는 템플릿 이름입니다: Duplicate Template"))

            // When & Then
            assertThrows<ResourceConflictException> {
                templateController.createTemplate(request)
            }

            verify(templateService, times(1)).createTemplate(any())
        }
    }

    @Nested
    inner class `템플릿 ID로 조회` {

        @Test
        fun `성공 - 200 OK 응답`() {
            // Given
            val templateId = "template-123"
            val response = createSampleResponse(id = templateId)
            whenever(templateService.getTemplateById(templateId)).thenReturn(response)

            // When
            val result = templateController.getTemplateById(templateId)

            // Then
            assertEquals(HttpStatus.OK, result.statusCode)
            assertNotNull(result.body)
            assertEquals(200, result.body?.httpStatus)
            assertEquals(templateId, result.body?.data?.id)
            assertEquals("Modern Template", result.body?.data?.name)
            assertEquals(LayoutType.SINGLE_COLUMN, result.body?.data?.layoutType)

            verify(templateService, times(1)).getTemplateById(templateId)
        }

        @Test
        fun `실패 - 존재하지 않는 ID 시 예외 발생`() {
            // Given
            val templateId = "non-existent-id"
            whenever(templateService.getTemplateById(templateId))
                .thenThrow(ResourceNotFoundException("템플릿을 찾을 수 없습니다. ID: $templateId"))

            // When & Then
            assertThrows<ResourceNotFoundException> {
                templateController.getTemplateById(templateId)
            }

            verify(templateService, times(1)).getTemplateById(templateId)
        }
    }

    @Nested
    inner class `상태별 템플릿 목록 조회` {

        @Test
        fun `성공 - ACTIVE 상태 200 OK 응답`() {
            // Given
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
            whenever(templateService.findAllTemplatesByStatus(status)).thenReturn(responses)

            // When
            val result = templateController.getTemplatesByStatus(status)

            // Then
            assertEquals(HttpStatus.OK, result.statusCode)
            assertNotNull(result.body)
            assertEquals(200, result.body?.httpStatus)
            assertEquals(2, result.body?.data?.size)
            assertEquals("template-1", result.body?.data?.get(0)?.id)
            assertEquals("Template 1", result.body?.data?.get(0)?.name)
            assertEquals("template-2", result.body?.data?.get(1)?.id)
            assertEquals("Template 2", result.body?.data?.get(1)?.name)

            verify(templateService, times(1)).findAllTemplatesByStatus(status)
        }

        @Test
        fun `성공 - INACTIVE 상태 빈 목록 200 OK 응답`() {
            // Given
            val status = TemplateStatus.INACTIVE
            whenever(templateService.findAllTemplatesByStatus(status)).thenReturn(emptyList())

            // When
            val result = templateController.getTemplatesByStatus(status)

            // Then
            assertEquals(HttpStatus.OK, result.statusCode)
            assertNotNull(result.body)
            assertEquals(200, result.body?.httpStatus)
            assertEquals(0, result.body?.data?.size)

            verify(templateService, times(1)).findAllTemplatesByStatus(status)
        }
    }
}
