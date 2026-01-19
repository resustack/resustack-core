package com.resustack.common.model

import io.swagger.v3.oas.annotations.media.Schema
import org.springframework.data.domain.Page

/**
 * 페이징 응답 래퍼
 * @param T 콘텐츠 타입
 */
@Schema(description = "페이징 응답")
data class PaginationResponse<T : Any>(
    @field:Schema(description = "콘텐츠 목록")
    val content: List<T>,

    @field:Schema(description = "전체 요소 개수", example = "42")
    val totalElements: Long,

    @field:Schema(description = "전체 페이지 개수", example = "5")
    val totalPages: Int,

    @field:Schema(description = "현재 페이지 번호 (0부터 시작)", example = "0")
    val currentPage: Int,

    @field:Schema(description = "페이지 크기", example = "10")
    val pageSize: Int,

    @field:Schema(description = "다음 페이지 존재 여부", example = "true")
    val hasNext: Boolean,

    @field:Schema(description = "이전 페이지 존재 여부", example = "false")
    val hasPrevious: Boolean
) {
    companion object {
        /**
         * Spring Data Page 객체를 PaginationResponse로 변환
         * @param page Spring Data Page 객체
         * @param transformer 엔티티를 DTO로 변환하는 함수
         */
        fun <E : Any, T : Any> from(page: Page<E>, transformer: (E) -> T): PaginationResponse<T> {
            return PaginationResponse(
                content = page.content.map(transformer),
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number,
                pageSize = page.size,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }

        /**
         * 이미 변환된 Page<T> 객체를 PaginationResponse로 변환
         * @param page 변환된 Page 객체
         */
        fun <T : Any> from(page: Page<T>): PaginationResponse<T> {
            return PaginationResponse(
                content = page.content,
                totalElements = page.totalElements,
                totalPages = page.totalPages,
                currentPage = page.number,
                pageSize = page.size,
                hasNext = page.hasNext(),
                hasPrevious = page.hasPrevious()
            )
        }
    }
}
