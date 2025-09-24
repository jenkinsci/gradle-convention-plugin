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

package io.github.aaravmahajanofficial

import io.github.aaravmahajanofficial.utils.TestProjectBuilder
import io.github.aaravmahajanofficial.utils.mockBuildScript
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Bom Management Integration Tests")
class BomManagementIntegrationTest {
    lateinit var builder: TestProjectBuilder

    @Test
    @DisplayName("should apply predefined BOMs correctly")
    fun `apply all predefined boms correctly`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(mockBuildScript())
                .withJavaSource()

        val result = builder.runGradle("dependencies", "--configuration=runtimeClasspath")

        result.task(":dependencies")?.outcome shouldBe TaskOutcome.SUCCESS

        val expectedBoms =
            listOf(
                "io.jenkins.tools.bom:bom-2.504.x:5015.vb_52d36583443",
                "org.springframework:spring-framework-bom:6.2.9",
                "com.fasterxml.jackson:jackson-bom:2.19.2",
                "io.netty:netty-bom:4.2.3.Final",
                "org.slf4j:slf4j-bom:2.0.17",
                "org.eclipse.jetty:jetty-bom:12.0.23",
                "com.google.guava:guava-bom:33.4.8-jre",
                "org.apache.logging.log4j:log4j-bom:2.25.1",
                "io.vertx:vertx-stack-depchain:5.0.1",
            )

        expectedBoms.forEach { bom ->
            result.output shouldContain bom
        }

        // test-only boms must not be present
        val testBoms =
            listOf(
                "org.junit:junit-bom",
                "org.mockito:mockito-bom",
                "org.testcontainers:testcontainers-bom",
            )
        testBoms.forEach { bom ->
            result.output shouldNotContain bom
        }
    }

    @Test
    @DisplayName("should handle test-only BOMs correctly")
    fun `handle test-only boms correctly`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(mockBuildScript())
                .withJavaSource()

        val result = builder.runGradle("dependencies", "--configuration=testRuntimeClasspath")

        result.task(":dependencies")?.outcome shouldBe TaskOutcome.SUCCESS

        result.output shouldContain "org.junit:junit-bom:5.13.4"
        result.output shouldContain "org.mockito:mockito-bom:5.18.0"
        result.output shouldContain "org.testcontainers:testcontainers-bom:1.21.3"
    }

    @Test
    @DisplayName("should apply custom BOM")
    fun `apply custom bom`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    mockBuildScript(
                        bomBlock =
                            """
                            bom {
                                customBoms {
                                    create("micrometer-bom") {
                                        coordinates = "io.micrometer:micrometer-bom"
                                        version = "1.15.4"
                                        testOnly = false
                                    }
                                }
                            }
                            """.trimIndent(),
                    ),
                ).withJavaSource()
                .withTestSource()

        val result = builder.runGradle("dependencies", "--configuration=runtimeClasspath")

        result.task(":dependencies")?.outcome shouldBe TaskOutcome.SUCCESS

        result.output shouldContain "io.micrometer:micrometer-bom:1.15.4"
    }

    @Test
    @DisplayName("should fail with clear error when coordinates are missing in custom BOM")
    fun `fail on custom bom missing coordinates`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    mockBuildScript(
                        bomBlock =
                            """
                            bom {
                                customBoms {
                                    create("micrometer-bom") {
                                        version = "1.15.4"
                                        testOnly = false
                                    }
                                }
                            }
                            """.trimIndent(),
                    ),
                ).withJavaSource()

        val result = builder.runGradleAndFail("help")

        result.output shouldContain "Missing coordinates for BOM 'micrometer-bom'."
    }

    @Test
    @DisplayName("should fail with clear error when version is missing in custom BOM")
    fun `fail on custom bom missing version`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    mockBuildScript(
                        bomBlock =
                            """
                            bom {
                                customBoms {
                                    create("micrometer-bom") {
                                        coordinates = "io.micrometer:micrometer-bom"
                                        testOnly = false
                                    }
                                }
                            }
                            """.trimIndent(),
                    ),
                ).withJavaSource()

        val result = builder.runGradleAndFail("help")

        result.output shouldContain "Missing version for BOM 'micrometer-bom'."
    }

    @Test
    @DisplayName("should handle bom conflicts gracefully")
    fun `handle bom conflicts gracefully`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    mockBuildScript(
                        dependenciesBlock =
                            """
                            dependencies {
                                implementation("org.springframework.boot:spring-boot-starter-json:2.7.8")
                                implementation("com.fasterxml.jackson.core:jackson-core:2.16.0")
                            }
                            """.trimIndent(),
                    ),
                ).withJavaSource()

        val result = builder.runGradle("dependencies", "--configuration=runtimeClasspath")

        result.task(":dependencies")?.outcome shouldBe TaskOutcome.SUCCESS

        result.output shouldContain "com.fasterxml.jackson:jackson-bom:2.19.2"
        result.output shouldContain "com.fasterxml.jackson.core:jackson-core:2.19.2"
    }

    @Test
    @DisplayName("should disable boms via properties")
    fun `disable boms via properties`() {
        builder =
            TestProjectBuilder
                .create()
                .withVersionCatalog()
                .withSettingsGradle()
                .withGradleProperties(
                    mapOf(
                        "bom.jenkins.enabled" to "false",
                        "bom.jackson.enabled" to "false",
                        "bom.spring.enabled" to "false",
                    ),
                ).withBuildGradle(mockBuildScript())
                .withJavaSource()

        val result = builder.runGradle("dependencies", "--configuration=runtimeClasspath")

        result.task(":dependencies")?.outcome shouldBe TaskOutcome.SUCCESS

        result.output shouldNotContain "io.jenkins.tools.bom:bom-2.504.x"
        result.output shouldNotContain "com.fasterxml.jackson:jackson-bom"
        result.output shouldNotContain "org.springframework:spring-framework-bom"
    }
}
