package com.resustack.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
@ComponentScan(basePackages = ["com.resustack.api", "com.resustack.common"])
@ConfigurationPropertiesScan(basePackages = ["com.resustack.api", "com.resustack.common"])
class ResustackApiApplication

fun main(args: Array<String>) {
    runApplication<ResustackApiApplication>(*args)
}
