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
import com.diffplug.spotless.LineEnding
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

    val headerFile =
        rootProject.layout.projectDirectory
            .file("config/spotless/license-header.txt")
            .asFile
    val ktlintVersion = versionFromCatalogOrFail(libs, "ktlint")
    val palantirJavaVersion = versionFromCatalogOrFail(libs, "palantir-java")
    val delimiter =
        "^\\s*(plugins|pluginManagement|import|buildscript|" +
            "dependencyResolutionManagement|enableFeaturePreview|include|rootProject)\\b"

    val commonExcludes =
        listOf(
            "**/build/**",
            "**/build-*/**",
            "**/.gradle/**",
            "**/.idea/**",
            "**/.git/**",
            "**/generated/**",
            "**/.gradle-test-kit/**",
            "**/node_modules/**",
        )

    configure<SpotlessExtension> {
        if (hasKotlinSources()) {
            kotlin { t ->
                t.target("**/*.kt")
                t.targetExclude(commonExcludes)
                t.ktlint(ktlintVersion)
                t.trimTrailingWhitespace()
                t.endWithNewline()
                t.lineEndings = LineEnding.UNIX

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile)
                }
            }
            kotlinGradle { t ->
                t.target("**/*.gradle.kts")
                t.targetExclude(commonExcludes)
                t.ktlint(ktlintVersion)
                t.trimTrailingWhitespace()
                t.endWithNewline()
                t.lineEndings = LineEnding.UNIX

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile, delimiter)
                }
            }
        }
        if (hasJavaSources()) {
            java { t ->
                t.target("**/*.java")
                t.targetExclude(commonExcludes)

                t.palantirJavaFormat(palantirJavaVersion)
                t.importOrder()
                t.removeUnusedImports()
                t.trimTrailingWhitespace()
                t.removeWildcardImports()
                t.endWithNewline()
                t.toggleOffOn()
                t.lineEndings = LineEnding.UNIX

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile)
                }
            }
        }
        if (hasGroovySources()) {
            groovy { t ->
                t.target("**/*.groovy")
                t.targetExclude(commonExcludes)

                t.greclipse()
                t.trimTrailingWhitespace()
                t.endWithNewline()
                t.lineEndings = LineEnding.UNIX

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile)
                }
            }
            groovyGradle { t ->
                t.target("**/*.gradle")
                t.targetExclude(commonExcludes)

                t.greclipse()
                t.trimTrailingWhitespace()
                t.endWithNewline()
                t.lineEndings = LineEnding.UNIX

                if (headerFile.exists()) {
                    t.licenseHeaderFile(headerFile, delimiter)
                }
            }
        }
        format("misc") { t ->
            t.target(
                "**/*.md",
                "**/*.properties",
                "**/*.yml",
                "**/*.yaml",
                "**/*.json",
                "**/*.xml",
                "**/*.sh",
                "**/*.txt",
                "**/.gitignore",
                "**/Dockerfile*",
                "**/*.dockerignore",
                "Jenkinsfile",
            )
            t.targetExclude(commonExcludes)
            t.trimTrailingWhitespace()
            t.endWithNewline()
            t.lineEndings = LineEnding.UNIX
        }
    }

    tasks.named("check").configure { t ->
        t.dependsOn("spotlessCheck")
    }
}
