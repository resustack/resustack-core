package com.resustack.api.config

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.models.GroupedOpenApi
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfig(
    @Value($$"${app.dev-server-url:}") private val devServerUrl: String
) {

    @Bean
    fun openAPI(): OpenAPI {
        val info = Info()
            .title("Resustack API Document")
            .version("1.0")
            .description("Resustack Core API 명세서입니다.")

        val localServer = Server()
            .url("http://localhost:8080")
            .description("Local development server")

        val servers = mutableListOf(localServer)
        if (devServerUrl.isNotBlank()) {
            servers.add(Server().url(devServerUrl).description("Develop server"))
        }

        val securityScheme = SecurityScheme()
            .type(SecurityScheme.Type.HTTP)
            .scheme("bearer")
            .bearerFormat("JWT")
            .`in`(SecurityScheme.In.HEADER)
            .name("Authorization")

        val securityRequirement = SecurityRequirement().addList("bearerAuth")

        return OpenAPI()
            .info(info)
            .servers(servers)
            .components(Components().addSecuritySchemes("bearerAuth", securityScheme))
            .addSecurityItem(securityRequirement)
    }

    @Bean
    fun templateApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("template-api")
            .pathsToMatch("/api/templates/**")
            .build()
    }

    @Bean
    fun resumeApi(): GroupedOpenApi {
        return GroupedOpenApi.builder()
            .group("resume-api")
            .pathsToMatch("/api/resumes/**")
            .build()
    }
}
