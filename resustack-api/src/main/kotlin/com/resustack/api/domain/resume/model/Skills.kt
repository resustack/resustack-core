package com.resustack.api.domain.resume.model

/**
 * 기술 스택 정보
 * 카테고리별로 기술을 관리 (language, framework, devOps 등)
 */
data class Skills(
    val devOps: List<String> = emptyList(),
    val language: List<String> = emptyList(),
    val framework: List<String> = emptyList(),
    val database: List<String> = emptyList(),
    val tool: List<String> = emptyList(),
    val library: List<String> = emptyList(),
    val testing: List<String> = emptyList(),
    val collaboration: List<String> = emptyList(),
    val etc: List<String> = emptyList()
)
