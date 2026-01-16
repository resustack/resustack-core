package com.resustack.api.config

import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.testcontainers.containers.MongoDBContainer
import org.testcontainers.utility.DockerImageName

/**
 * MongoDB Testcontainers 설정을 위한 추상 베이스 클래스
 *
 * 모든 통합 테스트는 이 클래스를 상속받아 실제 MongoDB 인스턴스를 사용한 테스트를 수행합니다.
 * Testcontainers는 Docker를 사용하여 테스트용 MongoDB를 자동으로 시작하고 종료합니다.
 */
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 비활성화
abstract class MongoTestContainerConfig {

    companion object {
        // MongoDB 7.0.15 이미지를 사용하는 컨테이너 (모든 테스트에서 공유)
        private val mongoDBContainer = MongoDBContainer(DockerImageName.parse("mongo:7.0.15"))
            .apply {
                start() // 컨테이너 시작
            }

        /**
         * 테스트 실행 시 Spring의 MongoDB URI를 Testcontainers의 URI로 동적 설정
         */
        @JvmStatic
        @DynamicPropertySource
        fun setProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.mongodb.uri") { mongoDBContainer.replicaSetUrl }
        }
    }
}
