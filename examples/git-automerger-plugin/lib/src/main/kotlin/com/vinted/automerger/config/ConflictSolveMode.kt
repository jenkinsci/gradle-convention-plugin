package com.vinted.automerger.config

/**
 * For example you have conflict:
 * ```
 * | release/1.0 | release/1.1 |
 * |-------------|-------------|
 * | Foo         | Bar         |
 * |-------------|-------------|
 * ```
 * According to selected resolution, one of outcome could be:
 *
 * ```
 * | [KEEP_OLDER] | [KEEP_NEWER] | [MERGE_NEWER_TOP] | [MERGE_OLDER_TOP] |
 * |--------------|--------------|-------------------|-------------------|
 * | Foo          | Bar          | Bar               | Foo               |
 * |--------------|--------------| Foo               | Bar               |
 *                               |-------------------|-------------------|
 * ```
 */
enum class Resolution(internal val impl: (new: String, old: String) -> String) {
    KEEP_OLDER({ _, old -> old }),
    KEEP_NEWER({ new, _ -> new }),
    MERGE_OLDER_TOP({ new, old -> old + "\n" + new }),
    MERGE_NEWER_TOP({ new, old -> new + "\n" + old })
}
