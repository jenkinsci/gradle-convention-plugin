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

            configureSpotless()
            configureDetekt(libs)

            tasks.named("check") { dependsOn("spotlessCheck", "detekt") }
        }
    }
}

private fun Project.configureSpotless() {
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**", "bin/**", "**/generated/**")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(rootProject.file("config/license-header.txt"), "(package |@file:|import )")
        }
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts", "settings.gradle.kts")
            targetExclude("**/build/**", "**/.gradle/**")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
            licenseHeaderFile(
                rootProject.file("config/license-header.txt").absolutePath,
                "^\\s*(pluginManagement|plugins|plugin|import|buildscript|dependencyResolutionManagement|enableFeaturePreview|include|rootProject|[a-zA-Z])",
            )
        }
        java {
            googleJavaFormat()
            target("src/*/java/**/*.java")
            targetExclude("**/generated/**", "**/build/**", "**/.gradle/**")
            trimTrailingWhitespace()
            endWithNewline()
            removeUnusedImports()
            licenseHeaderFile(rootProject.file("config/license-header.txt").absolutePath, "(package |@file:|import )")
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
        jvmTarget = JvmTarget.JVM_17.target
        autoCorrect = false

        reports {
            html.required.set(true)
            xml.required.set(true)
            sarif.required.set(true)
            txt.required.set(true)
        }
    }
}
