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
import org.gradle.api.tasks.testing.logging.TestExceptionFormat

plugins {
    `jvm-test-suite`
    id("conventions.kotlin")
    id("conventions.quality")
    alias(baseLibs.plugins.plugin.publish)
}

description = "Gradle plugin that provides conventions for developing Jenkins plugins"

dependencies {
    implementation(gradleApi())
    implementation(gradleKotlinDsl())
    implementation(baseLibs.kotlin.gradle.plugin)
    implementation(baseLibs.jenkins.gradle.jpi2)
    implementation(baseLibs.spotless.gradle.plugin)
    implementation(baseLibs.detekt.gradle.plugin)
    implementation(baseLibs.spotbugs.gradle.plugin)
    implementation(baseLibs.owasp.depcheck.gradle.plugin)
    implementation(baseLibs.benmanes.versions.gradle.plugin)
    implementation(baseLibs.pit.gradle.plugin)
    implementation(baseLibs.kover.gradle.plugin)
    implementation(baseLibs.node.gradle.plugin)
    implementation(baseLibs.dokka.gradle.plugin)
    implementation(baseLibs.cpd.gradle.plugin)
    implementation(baseLibs.ktlint.gradle.plugin) {
        exclude(group = "org.jetbrains.kotlin", module = "kotlin-compiler-embeddable")
    }
    implementation(baseLibs.jgit.gradle.plugin)
}

gradlePlugin {
    website = "https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin"
    vcsUrl = "https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin"
    plugins {
        create("jenkinsConventions") {
            id = "io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin"
            displayName = "Jenkins Gradle Convention Plugin"
            description = "Convention plugin for developing Jenkins plugins with Gradle"
            tags =
                listOf(
                    "jenkins",
                    "convention-plugin",
                    "jenkins-plugin-development",
                    "build-automation",
                    "standardization",
                    "build-logic",
                    "version-catalog",
                )
            implementationClass = "io.github.aaravmahajanofficial.JenkinsConventionPlugin"
        }
    }
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter()
        }

        register<JvmTestSuite>("integrationTest") {

            useJUnitJupiter()

            dependencies {
                implementation(project())
                implementation(gradleTestKit())
                implementation(baseLibs.kotest.assertions.core)
            }

            targets.configureEach {
                testTask.configure {

                    classpath += files(tasks.named("pluginUnderTestMetadata"))

                    shouldRunAfter(tasks.named("test"))

                    testLogging {
                        events("passed", "skipped", "failed", "standardOut", "standardError")
                        exceptionFormat = TestExceptionFormat.FULL
                        showExceptions = true
                        showCauses = true
                        showStackTraces = true
                    }

                    maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
                }
            }
        }
    }
}

tasks.named("check") {
    dependsOn(testing.suites.named("integrationTest"))
}

tasks.register("publishToLocal") {
    group = "Publishing"
    description = "Publishes the project to the local Maven repository"
    dependsOn("publishToMavenLocal")
}

tasks.withType<ValidatePlugins>().configureEach {
    failOnWarning.set(true)
    enableStricterValidation.set(true)
}
