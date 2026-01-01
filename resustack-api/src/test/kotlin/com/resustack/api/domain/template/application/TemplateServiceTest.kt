package com.resustack.api.domain.template.application

import com.resustack.api.domain.template.application.dto.TemplateCreateRequest
import com.resustack.api.domain.template.model.Spacing
import com.resustack.api.common.exception.BusinessException
import com.resustack.api.common.exception.ResourceConflictException
import com.resustack.api.common.exception.ResourceNotFoundException
import com.resustack.api.domain.template.model.*
import com.resustack.api.domain.template.repository.TemplateRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.*

class TemplateServiceTest {

    private lateinit var templateRepository: TemplateRepository
    private lateinit var templateService: TemplateService

    @BeforeEach
    fun setUp() {
        templateRepository = mock()
        templateService = TemplateService(templateRepository)
    }

    @Test
    fun `템플릿 생성 성공`() {
        // Given
        val request = TemplateCreateRequest(
            name = "Modern Template",
            description = "A modern resume template",
            thumbnail = "https://example.com/thumbnail.png",
            layoutType = LayoutType.SINGLE_COLUMN,
            theme = Theme(
                primaryColor = "#000000",
                secondaryColor = "#FFFFFF",
                fontFamily = "Pretendard",
                spacing = Spacing(base = "16px")
            ),
            defaultSections = emptyList()
        )

        val savedDomain = Template(
            id = "template-123",
            name = request.name,
            description = request.description,
            thumbnail = request.thumbnail,
            layoutType = request.layoutType,
            theme = Theme(
                primaryColor = request.theme.primaryColor,
                secondaryColor = request.theme.secondaryColor,
                fontFamily = request.theme.fontFamily,
                spacing = Spacing(base = request.theme.spacing.base)
            ),
            defaultSections = emptyList()
        )

        whenever(templateRepository.existsByName(request.name)).thenReturn(false)
        whenever(templateRepository.save(any())).thenReturn(savedDomain)

        // When
        val response = templateService.createTemplate(request)

        // Then
        assertNotNull(response)
        assertEquals("template-123", response.id)
        assertEquals("Modern Template", response.name)
        verify(templateRepository, times(1)).existsByName(request.name)
        verify(templateRepository, times(1)).save(any())
    }

    @Test
    fun `중복된 템플릿 이름으로 생성 시 예외 발생`() {
        // Given
        val request = TemplateCreateRequest(
            name = "Duplicate Template",
            layoutType = LayoutType.SINGLE_COLUMN,
            theme = Theme(
                primaryColor = "#000000",
                secondaryColor = "#FFFFFF"
            )
        )

        whenever(templateRepository.existsByName(request.name)).thenReturn(true)

        // When & Then
        assertThrows<ResourceConflictException> {
            templateService.createTemplate(request)
        }

        verify(templateRepository, times(1)).existsByName(request.name)
        verify(templateRepository, never()).save(any())
    }

    @Test
    fun `존재하지 않는 템플릿 조회 시 예외 발생`() {
        // Given
        val templateId = "non-existent-id"
        whenever(templateRepository.findById(templateId))
            .thenThrow(ResourceNotFoundException("템플릿을 찾을 수 없습니다. ID: $templateId"))

        // When & Then
        assertThrows<ResourceNotFoundException> {
            templateService.getTemplateById(templateId)
        }
    }
}
