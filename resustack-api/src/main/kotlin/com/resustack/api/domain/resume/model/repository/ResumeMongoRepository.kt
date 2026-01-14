package com.resustack.api.domain.resume.model.repository

import com.resustack.api.domain.resume.model.ResumeEntity
import org.springframework.data.mongodb.repository.MongoRepository

interface ResumeMongoRepository : MongoRepository<ResumeEntity, String> {
    fun findAllByUserId(userId: Long): List<ResumeEntity>
}
