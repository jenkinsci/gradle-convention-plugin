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
plugins {
    id("conventions.kotlin")
    id("conventions.quality")
    alias(libs.plugins.plugin.publish)
}

description = "Gradle plugin that provides conventions for developing Jenkins plugins"

group = project.property("group") as String
version = project.property("version") as String

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    compileOnly(libs.kotlin.gradle.plugin) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(libs.jenkins.gradle.jpi2)
    implementation(libs.spotless.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
    implementation(libs.spotbugs.gradle.plugin)
    implementation(libs.owasp.depcheck.gradle.plugin)
    implementation(libs.benmanes.versions.gradle.plugin)
    implementation(libs.pit.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.node.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
}

gradlePlugin {
    website = "https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin"
    vcsUrl = "https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin"
    plugins {
        create("jenkinsConventions") {
            id = "io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin"
            displayName = "Jenkins Gradle Convention Plugin"
            description = "Convention plugin for developing Jenkins plugins with Gradle"
            tags = listOf("jenkins", "gradle-plugin", "convention-plugin", "plugin", "ci-cd")
            implementationClass = "JenkinsConventionPlugin"
        }
    }
}

// publishing {
//    repositories {
//        maven {
//            name = "localPluginRepository"
//            url = uri("C:\\Users\\aarav\\Desktop/local-plugin-repository")
//        }
//    }
// }
