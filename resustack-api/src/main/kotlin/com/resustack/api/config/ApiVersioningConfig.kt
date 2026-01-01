package com.resustack.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.ApiVersionConfigurer
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 * API 버저닝 설정
 *
 * X-Api-Version 헤더를 통해 API 버전 지정
 */
@Configuration
class ApiVersioningConfig : WebMvcConfigurer {

    companion object {
        const val VERSION_HEADER = "X-Api-Version"
        const val DEFAULT_VERSION = "1.0"
        val API_VERSIONS = arrayOf("1.0")
    }

    override fun configureApiVersioning(configurer: ApiVersionConfigurer) {
        configurer
            .useRequestHeader(VERSION_HEADER)
            .setDefaultVersion(DEFAULT_VERSION)
            .addSupportedVersions(*API_VERSIONS)
    }
}
