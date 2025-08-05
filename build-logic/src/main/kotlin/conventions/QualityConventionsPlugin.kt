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
package conventions

import com.diffplug.gradle.spotless.SpotlessExtension
import conventions.FormattingConstants.BIN
import conventions.FormattingConstants.BUILD
import conventions.FormattingConstants.GENERATED
import conventions.FormattingConstants.GRADLE
import conventions.FormattingConstants.LIC_PATH
import conventions.FormattingConstants.OUT
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

public class QualityConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.diffplug.spotless")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            configureSpotless(libs)
            configureDetekt(libs)

            tasks.named("check") { dependsOn("spotlessCheck", "detekt") }
        }
    }
}

private fun Project.configureSpotless(libs: VersionCatalog) {
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude(BUILD, BIN, GENERATED, OUT)
            ktlint(libs.findVersion("ktlint").get().requiredVersion)
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(
                rootProject.file(LIC_PATH),
                "(package |@file:|import )",
            )
        }
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts", "settings.gradle.kts")
            targetExclude(BUILD, GRADLE, OUT)
            ktlint(libs.findVersion("ktlint").get().requiredVersion)
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(
                rootProject.file(LIC_PATH),
                "(plugins|pluginManagement|import|buildscript|dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
            )
        }
        java {
            palantirJavaFormat(libs.findVersion("palantir-java").get().requiredVersion)
            target("src/**/*.java")
            targetExclude(GENERATED, BUILD, GRADLE, OUT)
            trimTrailingWhitespace()
            endWithNewline()
            removeUnusedImports()
            licenseHeaderFile(
                rootProject.file(LIC_PATH),
                "(package |@file:|import )",
            )
        }
        format("misc") {
            target(
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
                "*.gradle",
                "*.sh",
                "*.dockerfile",
                "Dockerfile*",
            )
            targetExclude(
                "**/build/**",
                "**/.gradle/**",
                "**/.idea/**",
                "**/node_modules/**",
                "**/.git/**",
                "**/generated/**",
                "**/out/**",
            )

            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

private fun Project.configureDetekt(libs: VersionCatalog) {
    configure<DetektExtension> {
        toolVersion = libs.findVersion("detekt").get().requiredVersion
        parallel = true
        baseline = rootProject.file("config/detekt/detekt-baseline.xml")
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = JvmTarget.JVM_21.target
        autoCorrect = false

        reports {
            html.required.set(true)
            xml.required.set(true)
            sarif.required.set(true)
            txt.required.set(true)
        }
    }
}

private object FormattingConstants {
    const val BUILD = "**/build/**"
    const val GRADLE = "**/.gradle/**"
    const val OUT = "**/out/**"
    const val GENERATED = "**/generated/**"
    const val GIT = "**/.git/**"
    const val BIN = "**/bin/**"
    const val LIC_PATH = "config/license-header.txt"
}
