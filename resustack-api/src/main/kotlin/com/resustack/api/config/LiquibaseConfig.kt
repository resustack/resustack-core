package com.resustack.api.config

import com.resustack.common.util.logger
import jakarta.annotation.PostConstruct
import liquibase.command.CommandScope
import liquibase.ext.mongodb.database.MongoClientDriver
import liquibase.ext.mongodb.database.MongoConnection
import liquibase.ext.mongodb.database.MongoLiquibaseDatabase
import liquibase.resource.ClassLoaderResourceAccessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import kotlin.use

@Profile("!test")
@Configuration
class LiquibaseConfig(
    @Value("\${spring.mongodb.uri}") private val mongoUri: String,
    @Value("\${spring.liquibase.change-log}") private val changeLog: String
) {

    private val log by logger()

    @PostConstruct
    fun runLiquibase() {
        try {
            MongoConnection().use { connection ->
                val driver = MongoClientDriver()
                connection.open(mongoUri, driver, null)

                val database = MongoLiquibaseDatabase()
                database.connection = connection

                val resourceAccessor = ClassLoaderResourceAccessor()
                val changeLogPath = changeLog.removePrefix("classpath:")

                log.info("Starting Liquibase migration: {}", changeLogPath)

                CommandScope("update")
                    .addArgumentValue("database", database)
                    .addArgumentValue("changelogFile", changeLogPath)
                    .addArgumentValue("resourceAccessor", resourceAccessor)
                    .execute()

                log.info("Liquibase migration completed successfully")
            }
        } catch (e: Exception) {
            log.error("Liquibase migration failed", e)
            throw IllegalStateException("Failed to run Liquibase migration", e)
        }
    }
}