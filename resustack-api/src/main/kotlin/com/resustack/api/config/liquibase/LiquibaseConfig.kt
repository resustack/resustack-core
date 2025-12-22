package com.resustack.api.config.liquibase

import liquibase.command.CommandScope
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase
import liquibase.ext.mongodb.database.MongoConnection
import liquibase.ext.mongodb.database.MongoClientDriver
import liquibase.resource.ClassLoaderResourceAccessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import jakarta.annotation.PostConstruct

@Configuration
class LiquibaseConfig(
    @Value("\${spring.mongodb.uri}") private val mongoUri: String,
    @Value("\${spring.liquibase.change-log}") private val changeLog: String
) {

    @PostConstruct
    fun runLiquibase() {
        val connection = MongoConnection()
        val driver = MongoClientDriver()

        connection.open(mongoUri, driver, null)

        val database = MongoLiquibaseDatabase()
        database.connection = connection

        val resourceAccessor = ClassLoaderResourceAccessor()

        // "classpath:" 접두사 제거
        val changeLogPath = changeLog.removePrefix("classpath:")

        CommandScope("update")
            .addArgumentValue("database", database)
            .addArgumentValue("changelogFile", changeLogPath)
            .addArgumentValue("resourceAccessor", resourceAccessor)
            .execute()
    }
}
