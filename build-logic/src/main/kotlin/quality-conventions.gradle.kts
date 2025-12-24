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
import com.diffplug.spotless.LineEnding
import io.gitlab.arturbosch.detekt.Detekt
import io.gitlab.arturbosch.detekt.extensions.DetektExtension

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
            "**/build-*/**",
            "**/.gradle/**",
            "**/.idea/**",
            "**/.git/**",
            "**/generated/**",
            "**/.gradle-test-kit/**",
        )

    kotlin {
        target("**/*.kt")
        targetExclude(commonExcludes)
        ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(headerFile)
        lineEndings = LineEnding.UNIX
    }
    kotlinGradle {
        target("**/*.gradle.kts")
        targetExclude(commonExcludes)
        ktlint(ktlintVersion).setEditorConfigPath(editorConfig)
        trimTrailingWhitespace()
        endWithNewline()
        licenseHeaderFile(headerFile, delimiter)
        lineEndings = LineEnding.UNIX
    }
    format("misc") {
        target(
            "**/*.md",
            "**/*.properties",
            "**/*.yml",
            "**/*.yaml",
            "**/*.xml",
            "**/.gitignore",
            "**/*.txt",
        )
        targetExclude(commonExcludes)
        trimTrailingWhitespace()
        endWithNewline()
        lineEndings = LineEnding.UNIX
    }
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

tasks.withType<Detekt>().configureEach {
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
