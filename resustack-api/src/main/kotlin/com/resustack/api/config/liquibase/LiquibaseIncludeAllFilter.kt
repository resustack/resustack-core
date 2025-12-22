package com.resustack.api.config.liquibase

import liquibase.changelog.IncludeAllFilter
import java.io.File
import java.util.regex.Pattern

/**
 * 파일 컨밴션
 * - 번호_설명.확장자
 * - mongo 디렉토리 내에서는 .yml, .yaml 파일만 포함
 */
class LiquibaseIncludeAllFilter : IncludeAllFilter {

    companion object {
        private val MONGO_YAML_PATTERN = Pattern.compile("^[0-9]{2}_[a-z0-9_]+\\.(yml|yaml)$")
    }

    override fun include(changeLogPath: String?): Boolean {
        if (changeLogPath == null) {
            return false
        }

        val lowerPath = changeLogPath.replace('\\', '/').lowercase()
        val name = File(changeLogPath).name.lowercase()

        // ignore hidden files
        if (name.startsWith(".")) {
            return false
        }

        // mongo: only *.yml or *.yaml
        if (lowerPath.contains("/mongo/")) {
            return MONGO_YAML_PATTERN.matcher(name).matches()
        }

        // other paths: do not include by default
        return false
    }
}