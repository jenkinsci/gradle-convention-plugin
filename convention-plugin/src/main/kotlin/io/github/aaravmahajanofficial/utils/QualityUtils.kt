/*
 * Copyright 2025 Aarav Mahajan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
package io.github.aaravmahajanofficial.utils

import io.github.aaravmahajanofficial.internal.quality.QualityManager
import org.gradle.api.Project
import org.gradle.api.attributes.LibraryElements
import org.gradle.api.attributes.Usage
import org.gradle.api.file.RegularFile
import org.gradle.kotlin.dsl.named

internal fun Project.hasJavaSources(): Boolean = fileTree("src").matching { it.include("**/*.java") }.files.isNotEmpty()

internal fun Project.hasKotlinSources(): Boolean = fileTree("src").matching { it.include("**/*.kt") }.files.isNotEmpty()

internal fun Project.hasGroovySources(): Boolean =
    fileTree("src")
        .matching {
            it.include("**/*.groovy")
        }.files
        .isNotEmpty()

internal fun Project.isFrontendProject(): Boolean =
    listOf("package.json", "yarn.lock", "pnpm-lock.yaml").any { file(it).exists() } ||
        listOf("src/main/js", "src/main/ts", "src/main/webapp").any { file(it).isDirectory }

internal fun Project.resolveConfigFile(
    toolName: String,
    fileName: String,
): RegularFile {
    val configPath = "config/$toolName/$fileName"
    val userConfig = rootProject.layout.projectDirectory.file(configPath)

    if (userConfig.asFile.exists()) return userConfig

    val resourceUrl =
        QualityManager::class.java.classLoader.getResource("defaults/$toolName/$fileName")
            ?: error("Missing built-in $toolName config file in plugin resources: $fileName. This is a bug.")

    userConfig.asFile.parentFile.mkdirs()

    resourceUrl
        .openStream()
        .use { input -> userConfig.asFile.outputStream().use { output -> input.copyTo(output) } }

    return userConfig
}

internal fun Project.variantResolution(config: String) {
    configurations.matching { it.name.startsWith(config) }.configureEach {
        it.attributes { at ->
            at.attribute(
                LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE,
                objects.named(LibraryElements.JAR),
            )
            at.attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
        }
    }
}

internal fun isNonStable(version: String): Boolean {
    val stableKeyword = listOf("RELEASE", "FINAL", "GA").any { version.uppercase().contains(it) }
    val regex = "^[0-9,.v-]+(-r)?$".toRegex()
    val isStable = stableKeyword || regex.matches(version)
    return isStable.not()
}
