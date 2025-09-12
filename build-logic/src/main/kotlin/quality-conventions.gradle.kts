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
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.kotlin.dsl.configure
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("baseLibs")

configure<SpotlessExtension> {

    val commonExcludes = listOf(
        "**/build/**",
        "**/.gradle/**",
        "**/.idea/**",
        "**/node_modules/**",
        "**/.git/**",
        "**/generated/**",
        "**/out/**",
        "**/.gradle-test-kit/**"
    )

    val licensePath = "config/license-header.txt"

    kotlin {
        target(
            "src/main/kotlin/**/*.kt",
            "src/test/kotlin/**/*.kt"
        )
        targetExclude(commonExcludes)
        ktlint(libs.findVersion("ktlint").get().requiredVersion)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(
            rootProject.file(licensePath),
            "(package |@file:|import )",
        )
    }
    kotlinGradle {
        target(
            "*.gradle.kts",
            "**/*.gradle.kts",
            "settings.gradle.kts"
        )
        targetExclude(commonExcludes + "**/gradle/**")
        ktlint(libs.findVersion("ktlint").get().requiredVersion)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(
            rootProject.file(licensePath),
            "(plugins|pluginManagement|import|buildscript|dependencyResolutionManagement|enableFeaturePreview|include|rootProject)",
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
            "*.sh",
            "*.dockerfile",
            "Dockerfile*"
        )
        targetExclude(commonExcludes)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

configure<DetektExtension> {
    toolVersion = libs.findVersion("detekt").get().requiredVersion
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom(rootProject.file("config/detekt/detekt.yml"))
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

tasks.named("check") {
    dependsOn("spotlessCheck", "detekt")
}