package com.resustack.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.EnableMongoAuditing

import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import java.util.concurrent.TimeUnit
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer

@Configuration
@EnableMongoAuditing
class MongoConfig {
    @Bean
    fun transactionManager(dbFactory: MongoDatabaseFactory): MongoTransactionManager {
        return MongoTransactionManager(dbFactory)
    }

    @Bean
    fun mongoClientSettingsBuilderCustomizer(): MongoClientSettingsBuilderCustomizer {
        return MongoClientSettingsBuilderCustomizer { builder ->
            builder
                .readPreference(ReadPreference.secondaryPreferred())
                .writeConcern(WriteConcern.MAJORITY
                    .withJournal(true)
                    .withWTimeout(5, TimeUnit.SECONDS))
        }
    }
}
