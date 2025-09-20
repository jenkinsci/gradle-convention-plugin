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
import org.jetbrains.kotlin.gradle.dsl.JvmDefaultMode
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion

plugins {
    id("org.jetbrains.kotlin.jvm")
}

private val libs = extensions.getByType<VersionCatalogsExtension>().named("baseLibs")
private val kotlinVersion = libs.findVersion("kotlinLanguage").get().requiredVersion

kotlin {
    jvmToolchain(21)

    explicitApi()

    compilerOptions {
        apiVersion.set(KotlinVersion.fromVersion(kotlinVersion))
        languageVersion.set(KotlinVersion.fromVersion(kotlinVersion))
        jvmTarget.set(JvmTarget.JVM_21)

        allWarningsAsErrors.set(true)
        progressiveMode.set(true)
        optIn.add("kotlin.RequiresOptIn")
        jvmDefault.set(JvmDefaultMode.ENABLE)
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
        )
    }
}

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    compileOnly(libs.findLibrary("kotlin-gradle-plugin").get())
    implementation(platform(libs.findLibrary("kotlin-bom").get()))
    compileOnly(libs.findLibrary("jetbrains-annotations").get())
}
