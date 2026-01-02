package com.resustack.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.EnableAspectJAutoProxy

@SpringBootApplication
@EnableAspectJAutoProxy
class ResustackApiApplication

fun main(args: Array<String>) {
    runApplication<ResustackApiApplication>(*args)
}
