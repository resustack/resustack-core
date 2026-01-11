package com.resustack.auth

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication(scanBasePackages = ["com.resustack.auth", "com.resustack.common"])
@EntityScan(basePackages = ["com.resustack.auth", "com.resustack.common"])
@EnableJpaRepositories(basePackages = ["com.resustack.auth", "com.resustack.common"])
class ResustackAuthApplication

fun main(args: Array<String>) {
    runApplication<ResustackAuthApplication>(*args)
}
