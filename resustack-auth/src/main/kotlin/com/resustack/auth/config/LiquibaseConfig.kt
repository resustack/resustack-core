package com.resustack.auth.config

import com.resustack.common.util.logger
import jakarta.annotation.PostConstruct
import liquibase.Liquibase
import liquibase.database.DatabaseFactory
import liquibase.database.jvm.JdbcConnection
import liquibase.resource.ClassLoaderResourceAccessor
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration
import java.sql.DriverManager

@Configuration
class LiquibaseConfig(
    @Value("\${spring.datasource.url}") private val jdbcUrl: String,
    @Value("\${spring.datasource.username}") private val username: String,
    @Value("\${spring.datasource.password}") private val password: String,
    @Value("\${spring.liquibase.change-log}") private val changeLog: String
) {

    private val log by logger()

    @PostConstruct
    fun runLiquibase() {
        try {
            DriverManager.getConnection(jdbcUrl, username, password).use { connection ->
                val database = DatabaseFactory.getInstance()
                    .findCorrectDatabaseImplementation(JdbcConnection(connection))

                val resourceAccessor = ClassLoaderResourceAccessor()
                val changeLogPath = changeLog.removePrefix("classpath:")

                log.info("Starting Liquibase migration: {}", changeLogPath)

                Liquibase(changeLogPath, resourceAccessor, database).use { liquibase ->
                    liquibase.update("")
                }

                log.info("Liquibase migration completed successfully")
            }
        } catch (e: Exception) {
            log.error("Liquibase migration failed", e)
            throw IllegalStateException("Failed to run Liquibase migration", e)
        }
    }
}

