package com.vinted.automerger.utils


internal fun String?.checkNotEmpty(errorMessage: String): String {
    if (this == null || isEmpty()) {
        throw IllegalArgumentException(errorMessage)
    } else {
        return this
    }
}

internal fun <T : Any> T?.checkNotNull(errorMessage: String): T {
    return this ?: throw IllegalArgumentException(errorMessage)
}
