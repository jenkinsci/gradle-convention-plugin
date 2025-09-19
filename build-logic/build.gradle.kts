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
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

val javaToolchainVersion: Provider<Int> =
    providers.gradleProperty("java.toolchain.version").map(String::toInt).orElse(21)

java {
    toolchain {
        languageVersion = javaToolchainVersion.map(JavaLanguageVersion::of)
    }
}

val kotlinVersion = KotlinVersion.fromVersion(baseLibs.versions.kotlinLanguage.get())

kotlin {
    jvmToolchain {
        languageVersion = javaToolchainVersion.map(JavaLanguageVersion::of)
    }

    explicitApi()

    compilerOptions {
        apiVersion.set(kotlinVersion)
        languageVersion.set(kotlinVersion)
        jvmTarget = JvmTarget.JVM_21

        allWarningsAsErrors.set(true)
        progressiveMode.set(true)
        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
        )
    }
}

dependencies {
    implementation(plugin(baseLibs.plugins.kotlin.jvm))
    implementation(plugin(baseLibs.plugins.spotless))
    implementation(plugin(baseLibs.plugins.detekt))
}

// Helper function that transforms a Gradle Plugin alias from a
// Version Catalog into a valid dependency notation for buildSrc
// See https://docs.gradle.org/current/userguide/version_catalogs.html#sec:buildsrc-version-catalog
fun plugin(plugin: Provider<PluginDependency>) =
    plugin.map { "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}" }
