package com.resustack.api.config

import com.resustack.common.security.filter.JwtAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.oauth2.client.web.OAuth2LoginAuthenticationFilter
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val corsConfigurationSource: CorsConfigurationSource,
    private val jwtAuthenticationFilter: JwtAuthenticationFilter
) {

    companion object {
        private val PUBLIC_ENDPOINTS = arrayOf(
            "/api/resumes/{id}",
            "/v3/api-docs/**",
            "/swagger-ui/**",
            "/swagger-resources/**",
            "/webjars/**"
        )
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors { it.configurationSource(corsConfigurationSource) }
            .csrf { it.disable() }
            .authorizeHttpRequests { auth ->
                auth
                    .requestMatchers(*PUBLIC_ENDPOINTS).permitAll()
                     .requestMatchers("/actuator/health").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2Login { }
            .oauth2Client { }
            .addFilterBefore(jwtAuthenticationFilter, OAuth2LoginAuthenticationFilter::class.java)

        return http.build()
    }
}
