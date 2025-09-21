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
import com.diffplug.gradle.spotless.SpotlessTask
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("com.diffplug.spotless")
    id("io.gitlab.arturbosch.detekt")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("baseLibs")

private val headerFile = rootProject.layout.projectDirectory.file("config/license-header.txt")
private val editorConfig = rootProject.layout.projectDirectory.file(".editorconfig")
private val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
private val delimiter =
    "^\\s*(plugins|pluginManagement|import|buildscript|" +
        "dependencyResolutionManagement|enableFeaturePreview|include|rootProject)\\b"

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
            "**/.kotlin/**",
            "**/bin/**",
        )

    kotlin {
        target("**/*.kt")
        targetExclude(commonExcludes)
        ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(headerFile)
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude(commonExcludes)
        ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(headerFile, delimiter)
    }
    format("misc") {
        target(
            "**/*.md",
            "**/*.properties",
            "**/*.yml",
            "**/*.yaml",
            "**/*.xml",
            "**/*.txt",
        )
        targetExclude(commonExcludes)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.withType<SpotlessTask>().configureEach {
    notCompatibleWithConfigurationCache("Spotless serialization issue")
}

private val detektConfig = rootProject.layout.projectDirectory.file("config/detekt/detekt.yml")
private val detektBaseline =
    rootProject.layout.projectDirectory
        .file("config/detekt/detekt-baseline.xml")
        .asFile

configure<DetektExtension> {
    toolVersion = libs.findVersion("detekt").get().requiredVersion
    parallel = true
    buildUponDefaultConfig = true
    config.setFrom(detektConfig)
    baseline = detektBaseline
}

tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
    jvmTarget = JvmTarget.JVM_21.target
    autoCorrect = false

    reports {
        xml.required.set(true)
        html.required.set(true)
        sarif.required.set(true)
        txt.required.set(false)
        md.required.set(false)
    }
}

tasks.named("check") {
    dependsOn("spotlessCheck", "detekt")
}
