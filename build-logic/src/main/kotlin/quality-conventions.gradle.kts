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
import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.gradle.spotless.SpotlessTaskImpl
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("baseLibs")

private val licensePath = rootProject.layout.projectDirectory.file("config/license-header.txt")
private val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion

configure<SpotlessExtension> {

    val commonExcludes =
        listOf(
            "**/build/**",
            "**/.gradle/**",
            "**/.idea/**",
            "**/.git/**",
            "**/generated/**",
            "**/out/**",
            "**/.gradle-test-kit/**",
            "**/gradle/**",
        )

    kotlin {
        target(
            "src/main/kotlin/**/*.kt",
            "src/test/kotlin/**/*.kt",
        )
        targetExclude(commonExcludes)
        ktlint(ktlintVersion)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(
            licensePath,
            "(package |@file:|import )",
        )
    }
    kotlinGradle {
        target(
            "*.gradle.kts",
            "**/*.gradle.kts",
            "settings.gradle.kts",
        )
        targetExclude(commonExcludes)
        ktlint(ktlintVersion)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(
            licensePath,
            "(plugins|pluginManagement|import|buildscript|dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
        )
    }
    format("misc") {
        target(
            "**/*.md",
            "**/*.txt",
            "**/*.gitignore",
            "**/*.gitattributes",
            "**/*.properties",
            "**/*.yml",
            "**/*.yaml",
            "**/*.editorconfig",
            "**/*.xml",
        )
        targetExclude(commonExcludes)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.withType<SpotlessTaskImpl>().configureEach {
    notCompatibleWithConfigurationCache("Spotless serialization issue")
}

configure<DetektExtension> {
    toolVersion = libs.findVersion("detekt").get().requiredVersion
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom(rootProject.layout.projectDirectory.file("config/detekt/detekt.yml"))
    baseline =
        rootProject.layout.projectDirectory
            .file("config/detekt/detekt-baseline.xml")
            .asFile
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
    autoCorrect = false

    reports {
        html.required.set(true)
        xml.required.set(true)
    }
}

tasks.named("check") {
    dependsOn("spotlessCheck", "detekt")
}
