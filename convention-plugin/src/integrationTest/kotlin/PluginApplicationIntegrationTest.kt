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
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.TestProjectBuilder

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Plugin Application Integration Tests!")
class PluginApplicationIntegrationTest {
    @Test
    @DisplayName("should apply convention plugin to new Kotlin DSL project without errors")
    fun `apply plugin to new Kotlin DSL project`() {
        val result =
            TestProjectBuilder
                .create()
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

                        licenses {
                            license {
                                name = "MIT"
                                url = uri("https://opensource.org/license/mit")
                            }
                        }
                    }
                    """.trimIndent(),
                ).withJavaSource()
                .runGradle("help")

        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldNotContain "FAILED"
        result.output shouldNotContain "Exception"
    }

    @Test
    @DisplayName("should apply convention plugin to existing Java project without conflicts")
    fun `apply plugin to existing Java project`() {
        val result =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        java
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

                        licenses {
                            license {
                                name = "MIT"
                                url = uri("https://opensource.org/license/mit")
                            }
                        }
                    }
                    """.trimIndent(),
                ).withJavaSource()
                .runGradle("tasks", "--all")

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldContain ("jpi")
        result.output shouldContain ("test")
    }

    @Test
    @DisplayName("should validate minimum Gradle version requirement")
    fun `validate minimum gradle version`() {
        val result =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    jenkinsConvention {
                        artifactId = "version-test-plugin"
                        homePage = uri("https://github.com")

                        developers {
                            developer {
                                id = "dev-123"
                                name = "Test Dev"
                                email = "testDev@gmail.com"
                            }
                        }

                        licenses {
                            license {
                                name = "MIT"
                                url = uri("https://opensource.org/license/mit")
                            }
                        }
                    }
                    """.trimIndent(),
                ).runGradle("help")

        result.task(":help")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    @Test
    @DisplayName("should handle plugin application with property-based configuration")
    fun `apply plugin with property configuration`() {
        val result =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withGradleProperties(
                    mapOf(
                        "cfg.plg.artifactId" to "property-plugin-test",
                        "cfg.plg.jenkinsVersion" to "2.520",
                        "cfg.quality.jacoco.enabled" to "enabled",
                        "cfg.bom.jenkins" to "enabled",
                    ),
                ).withBuildGradle(
                    """
                    plugins {
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    jenkinsConvention {
                        humanReadableName = "Test Plugin"
                        homePage = uri("https://github.com")

                        developers {
                            developer {
                                id = "dev-123"
                                name = "Test Dev"
                                email = "testDev@gmail.com"
                            }
                        }
                    }
                    """.trimIndent(),
                ).withJavaSource()
                .runGradle("jenkinsConventionPluginInfo")

        result.output shouldContain ("property-plugin-test")
    }

    @Test
    @DisplayName("verify quality tasks are not created when disabled")
    fun `verify quality tasks not created when disabled`() {
        val result =
            TestProjectBuilder
                .create()
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
                .runGradle("tasks", "--all")

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS

        result.output shouldNotContain ("checkstyle")
        result.output shouldNotContain ("pmd")
    }
}
