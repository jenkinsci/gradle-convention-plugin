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
package io.github.aaravmahajanofficial.internal.quality

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessPlugin
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.hasGroovySources
import io.github.aaravmahajanofficial.utils.hasJavaSources
import io.github.aaravmahajanofficial.utils.hasKotlinSources
import io.github.aaravmahajanofficial.utils.variantResolution
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure

internal fun Project.configureSpotless(
    quality: QualityExtension,
    libs: VersionCatalog,
) {
    if (!quality.spotless.enabled.get()) return

    pluginManager.apply(SpotlessPlugin::class.java)

    variantResolution("spotless")

    val headerFile = rootProject.file("config/spotless/license-header.txt")

    val commonExcludes =
        listOf(
            "**/build/**",
            "**/.gradle/**",
            "**/.idea/**",
            "**/node_modules/**",
            "**/.git/**",
            "**/generated/**",
            "**/out/**",
            "**/.gradle-test-kit/**",
        )

    configure<SpotlessExtension> {
        if (hasKotlinSources()) {
            kotlin { t ->
                t.target(
                    "src/main/kotlin/**/*.kt",
                    "src/test/kotlin/**/*.kt",
                    "src/main/resources/**/*.kt",
                )
                t.targetExclude(commonExcludes)
                t.ktlint(versionFromCatalogOrFail(libs, "ktlint"))
                t.trimTrailingWhitespace()
                t.endWithNewline()

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile)
                }
            }
            kotlinGradle { t ->
                t.target(
                    "*.gradle.kts",
                    "**/*.gradle.kts",
                    "settings.gradle.kts",
                )
                t.targetExclude(commonExcludes + "**/gradle/**")
                t.ktlint(versionFromCatalogOrFail(libs, "ktlint"))
                t.trimTrailingWhitespace()
                t.endWithNewline()

                if (headerFile.exists()) {
                    t.licenseHeaderFile(
                        headerFile,
                        "(plugins|pluginManagement|import|buildscript|" +
                            "dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
                    )
                }
            }
        }
        if (hasJavaSources()) {
            java { t ->
                t.target(
                    "src/main/java/**/*.java",
                    "src/test/java/**/*.java",
                    "src/main/resources/**/*.java",
                )
                t.targetExclude(commonExcludes + "**/gradle/**")
                t.palantirJavaFormat(versionFromCatalogOrFail(libs, "palantir-java"))
                t.trimTrailingWhitespace()
                t.endWithNewline()
                t.removeUnusedImports()

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile)
                }
            }
        }
        if (hasGroovySources()) {
            groovy { t ->
                t.target(
                    "src/main/groovy/**/*.groovy",
                    "src/test/groovy/**/*.groovy",
                    "src/main/resources/**/*.groovy",
                )
                t.targetExclude(commonExcludes + "**/gradle**")

                t.greclipse()
                t.trimTrailingWhitespace()
                t.endWithNewline()

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile)
                }
            }
            groovyGradle { t ->
                t.target(
                    "*.gradle",
                    "**/*.gradle",
                    "settings.gradle",
                )
                t.targetExclude(commonExcludes + "**/gradle/**")

                t.greclipse()
                t.trimTrailingWhitespace()
                t.endWithNewline()

                if (headerFile.exists()) {
                    t.licenseHeaderFile(
                        headerFile,
                        "(plugins|pluginManagement|import|buildscript|" +
                            "dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
                    )
                }
            }
        }
        format("misc") { t ->
            t.target(
                "*.md",
                "*.txt",
                ".gitignore",
                ".gitattributes",
                "*.properties",
                "*.yml",
                "*.yaml",
                "*.json",
                ".editorconfig",
                "*.xml",
                "*.sh",
                "*.dockerfile",
                "Dockerfile*",
                ".env",
                ".dockerignore",
            )
            t.targetExclude(commonExcludes)
            t.trimTrailingWhitespace()
            t.endWithNewline()
        }
    }

    tasks.named("check").configure { t ->
        t.dependsOn("spotlessCheck")
    }
}
