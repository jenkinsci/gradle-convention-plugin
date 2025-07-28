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
@file:Suppress("FunctionName")

import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.TestProjectBuilder
import utils.basicBuildScript
import utils.basicPluginConfiguration

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Plugin Application Integration Tests")
class PluginApplicationIntegrationTest {
    lateinit var builder: TestProjectBuilder

    @AfterEach
    fun cleanupTestProject() {
        builder.cleanup()
    }

    @Test
    @DisplayName("should apply convention plugin to new Kotlin DSL project without errors")
    fun `apply plugin to new Kotlin DSL project`() {
        builder =
            TestProjectBuilder
                .create("kotlin-dsl-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource()

        val result = builder.runGradle("help")

        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldNotContain "FAILED"
        result.output shouldNotContain "Exception"
    }

    @Test
    @DisplayName("should apply convention plugin to existing Java project without conflicts")
    fun `apply plugin to existing Java project`() {
        builder =
            TestProjectBuilder
                .create("java-existing-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        java
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                     ${basicPluginConfiguration()}
                    """.trimIndent(),
                ).withJavaSource()

        val result = builder.runGradle("tasks", "--group=build")

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldContain "jpi"
        result.output shouldContain "test"
        result.output shouldContain "jar"
    }

    @Test
    @DisplayName("should validate minimum Gradle version requirement")
    fun `validate minimum gradle version`() {
        builder =
            TestProjectBuilder
                .create("gradle-version-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())

        val result = builder.runGradle("help")

        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldNotContain "requires Gradle"
    }

    @Test
    @DisplayName("should handle plugin application with property-based configuration")
    fun `apply plugin with property configuration`() {
        builder =
            TestProjectBuilder
                .create("property-config-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withGradleProperties(
                    mapOf(
                        "cfg.plg.artifactId" to "property-plugin-test",
                        "cfg.plg.jenkinsVersion" to "2.520",
                        "cfg.quality.jacoco.enabled" to "enabled",
                        "cfg.bom.jenkins" to "enabled",
                    ),
                ).withBuildGradle(basicBuildScript())
                .withJavaSource()

        val result = builder.runGradle("jenkinsConventionPluginInfo")

        result.task(":jenkinsConventionPluginInfo")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldContain "test-plugin"
    }

    @Test
    @DisplayName("should verify quality tasks are conditionally created")
    fun `verify quality tasks conditional creation`() {
        builder =
            TestProjectBuilder
                .create("quality-conditional-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    jenkinsConvention {
                        artifactId = "test-plugin"
                        humanReadableName = "Test Plugin"
                        homePage = uri("https://github.com")

                        developers {
                            developer {
                                id = "dev-123"
                                name = "Test Dev"
                                email = "testDev@gmail.com"
                            }
                        }
                        quality {
                            checkstyle {
                                enabled = false
                            }
                            pmd {
                                enabled = false
                            }
                        }
                    }
                    """.trimIndent(),
                ).withJavaSource()

        val result = builder.runGradle("tasks", "--all")

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS

        result.output shouldNotContain "checkstyle"
        result.output shouldNotContain "pmd"
    }

//    @Test
//    @DisplayName("should handle multi-module project structure")
//    fun `handle multi module project structure`() {
//        builder =
//            TestProjectBuilder
//                .create("multi-module-test")
//                .withVersionCatalog()
//                .withSettingsGradle()
//                .withBuildGradle(
//                    """
//                    plugins {
//                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") apply false
//                    }
//
//                    subprojects {
//
//                       plugins.apply("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
//
//                       jenkinsConvention {
//                            artifactId = "multi-module-project"
//                            humanReadableName = "Multi Module Test Plugin"
//                            homePage = uri("https://github.com")
//
//                            developers {
//                                developer {
//                                    id = "dev-123"
//                                    name = "Test Dev"
//                                    email = "testDev@gmail.com"
//                                }
//                            }
//                       }
//                    }
//
//                    """.trimIndent(),
//                ).withSubProject("plugin-core") {
//                    withBuildGradle(
//                        """
//
//                        """.trimIndent(),
//                    ).withJavaSource("com.example.core", "CoreLogic")
//                }.withSubProject("plugin-ui") {
//                    withBuildGradle(
//                        """
//
//                        """.trimIndent(),
//                    ).withJavaSource("com.example.ui", "UiLogic")
//                }
//
//        val result = builder.runGradle("projects")
//
//        result.task(":projects")?.outcome shouldBe TaskOutcome.SUCCESS
//        result.output shouldContain "plugin-core"
//        result.output shouldContain "plugin-ui"
//    }
}
