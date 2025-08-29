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

plugins {
    `java-gradle-plugin`
    alias(baseLibs.plugins.kotlin.dsl)
}

val javaToolchainVersion: Provider<Int> =
    providers.gradleProperty("java.toolchain.version").map(String::toInt).orElse(21)

java {
    toolchain {
        languageVersion = javaToolchainVersion.map(JavaLanguageVersion::of)
    }
}

kotlin {
    jvmToolchain {
        languageVersion = javaToolchainVersion.map(JavaLanguageVersion::of)
    }

    explicitApi()

    compilerOptions {
        apiVersion =
            org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_2
        jvmTarget = JvmTarget.JVM_21

        allWarningsAsErrors.set(true)
        progressiveMode.set(false)

        optIn.add("kotlin.RequiresOptIn")
        freeCompilerArgs.addAll(
            "-Xjsr305=strict",
            "-Xjvm-default=all",
        )
    }
}

gradlePlugin {
    plugins {
        create("javaConventions") {
            id = "conventions.java"
            displayName = "Java Conventions"
            implementationClass = "conventions.JavaConventionsPlugin"
        }
        create("kotlinConventions") {
            id = "conventions.kotlin"
            displayName = "Kotlin Conventions"
            implementationClass = "conventions.KotlinConventionsPlugin"
        }
        create("qualityConventions") {
            id = "conventions.quality"
            displayName = "Quality Conventions"
            implementationClass = "conventions.QualityConventionsPlugin"
        }
    }
}

dependencies {
    implementation(gradleApi())
    implementation(baseLibs.kotlin.gradle.plugin)
    implementation(baseLibs.spotless.gradle.plugin)
    implementation(baseLibs.detekt.gradle.plugin)
    implementation(baseLibs.ktlint.gradle.plugin)
}
