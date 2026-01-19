package com.resustack.api.config

import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.MongoTransactionManager
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.data.mongodb.core.MongoTemplate

import com.mongodb.ReadPreference
import com.mongodb.WriteConcern
import java.util.concurrent.TimeUnit
import org.springframework.boot.mongodb.autoconfigure.MongoClientSettingsBuilderCustomizer

@Configuration
@EnableMongoAuditing
class MongoConfig {
    @Bean
    fun transactionManager(mongoDbFactory: MongoDatabaseFactory): MongoTransactionManager {
        return MongoTransactionManager(mongoDbFactory)
    }

    /**
     * 기본 MongoTemplate (Primary)
     * - 모든 트랜잭션 작업에 사용
     * - ReadPreference: primary
     */
    @Primary
    @Bean
    fun mongoTemplate(mongoDbFactory: MongoDatabaseFactory): MongoTemplate {
        return MongoTemplate(mongoDbFactory)
    }

    /**
     * 기본 MongoDB 클라이언트 설정
     * - ReadPreference: primary (트랜잭션 호환성 보장)
     * - WriteConcern: MAJORITY (데이터 무결성 보장)
     */
    @Bean
    fun mongoClientSettingsBuilderCustomizer(): MongoClientSettingsBuilderCustomizer {
        return MongoClientSettingsBuilderCustomizer { builder ->
            builder
                .readPreference(ReadPreference.primary())
                .writeConcern(WriteConcern.MAJORITY
                    .withJournal(true)
                    .withWTimeout(5, TimeUnit.SECONDS))
        }
    }

    /**
     * Secondary Preferred 전용 MongoTemplate
     *
     * 사용 케이스:
     * - 대량 조회 (페이지네이션, 통계)
     * - 실시간성이 덜 중요한 읽기 작업
     * - 트랜잭션이 필요 없는 조회
     *
     * 주의사항:
     * - @Transactional과 함께 사용하면 에러 발생
     * - Replication Lag로 인해 약간의 지연 가능
     */
    @Bean("secondaryPreferredMongoTemplate")
    fun secondaryPreferredMongoTemplate(mongoDbFactory: MongoDatabaseFactory): MongoTemplate {
        val template = MongoTemplate(mongoDbFactory)
        template.readPreference = ReadPreference.secondaryPreferred()
        return template
    }
}
