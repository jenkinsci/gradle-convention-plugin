package com.vinted.automerger.config

import java.io.Serializable

/**
 * @param path must be relative path to file. Patterns is not supported
 * @param resolution see [Resolution]
 */
data class MergeRule(val path: String, val resolution: Resolution) : Serializable
