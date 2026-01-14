package com.resustack.api.domain.resume.model

import io.swagger.v3.oas.annotations.media.Schema

/**
 * 기술 스택 정보
 * 카테고리별로 기술을 관리 (language, framework, devOps 등)
 */
@Schema(description = "기술 스택 정보")
data class Skills(
    @Schema(description = "DevOps 기술 목록", example = "[\"Docker\", \"Kubernetes\", \"AWS\"]")
    val devOps: List<String> = emptyList(),

    @Schema(description = "프로그래밍 언어 목록", example = "[\"Java\", \"Kotlin\", \"Python\"]")
    val language: List<String> = emptyList(),

    @Schema(description = "프레임워크 목록", example = "[\"Spring Boot\", \"React\", \"Next.js\"]")
    val framework: List<String> = emptyList(),

    @Schema(description = "데이터베이스 목록", example = "[\"PostgreSQL\", \"MongoDB\", \"Redis\"]")
    val database: List<String> = emptyList(),

    @Schema(description = "도구 목록", example = "[\"IntelliJ\", \"Git\", \"Postman\"]")
    val tool: List<String> = emptyList(),

    @Schema(description = "라이브러리 목록", example = "[\"JPA\", \"QueryDSL\", \"Redux\"]")
    val library: List<String> = emptyList(),

    @Schema(description = "테스트 도구 목록", example = "[\"JUnit\", \"Jest\", \"Cypress\"]")
    val testing: List<String> = emptyList(),

    @Schema(description = "협업 도구 목록", example = "[\"Jira\", \"Slack\", \"Notion\"]")
    val collaboration: List<String> = emptyList(),

    @Schema(description = "기타 기술 목록", example = "[\"Linux\", \"Shell Script\"]")
    val etc: List<String> = emptyList()
)
