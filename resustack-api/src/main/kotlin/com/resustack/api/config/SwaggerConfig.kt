package com.resustack.api.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig {

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("Resustack API Document")
            .version("1.0")
            .description("Resustack 템플릿 관리 API 명세서입니다.")

        val localServer = Server()
            .url("http://localhost:8080")
            .description("Local development server")

        return OpenAPI()
            .info(info)
            .servers(listOf(localServer))
    }

    @Bean
    fun templateApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("template-api")
            .pathsToMatch("/api/templates/**")
            .build()
    }
}
