package com.resustack.common.util

import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * 로거 생성 확장 함수
 */
inline fun <reified T> T.logger(): Lazy<Logger> {
    return lazy { LoggerFactory.getLogger(T::class.java) }
}
