package com.vinted.automerger.plugin

import org.slf4j.spi.LocationAwareLogger

enum class LogLevel {
    TRACE, DEBUG, INFO, WARN, ERROR;

    val levelInt: Int
        get() = when (this) {
            TRACE -> LocationAwareLogger.TRACE_INT
            DEBUG -> LocationAwareLogger.DEBUG_INT
            INFO -> LocationAwareLogger.INFO_INT
            WARN -> LocationAwareLogger.WARN_INT
            ERROR -> LocationAwareLogger.ERROR_INT
        }
}
