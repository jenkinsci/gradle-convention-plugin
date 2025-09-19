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
package io.github.aaravmahajanofficial

import io.github.aaravmahajanofficial.utils.TestProjectBuilder
import io.github.aaravmahajanofficial.utils.basicPluginConfiguration
import io.kotest.matchers.shouldBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Frontend Setup Integration Tests")
class FrontendSetupIntegrationTest {

    lateinit var builder: TestProjectBuilder

    @Test
    @DisplayName("should register frontend tasks when frontend project is detected")
    fun `register frontend tasks`() {
        builder = TestProjectBuilder
            .create()
            .withVersionCatalog()
            .withSettingsGradle()
            .withBuildGradle(basicPluginConfiguration())
            .withPackageJson()
            .withJavaScriptSource()

        val result = builder.runGradle("tasks", "--all")

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldContain "frontendBuild"
        result.output shouldContain "frontendTest"
        result.output shouldContain "frontendLint"
        result.output shouldContain "frontendDev"
    }

    @Test
    @DisplayName("should not register frontend tasks when frontend assets or package managers found")
    fun `do not register frontend tasks when not a frontend project`() {
        builder = TestProjectBuilder
            .create()
            .withVersionCatalog()
            .withSettingsGradle()
            .withBuildGradle(basicPluginConfiguration())

        val result = builder.runGradle("tasks", "--all")

        result.task(":tasks")?.outcome shouldBe TaskOutcome.SUCCESS
        result.output shouldNotContain "frontendBuild"
        result.output shouldNotContain "frontendTest"
        result.output shouldNotContain "frontendLint"
        result.output shouldNotContain "frontendDev"
    }

    @Test
    @DisplayName("processResources should depend on frontendBuild")
    fun `processResources depend on frontendBuild`() {
        runDryRunWithScripts("processResources", "frontendBuild")
    }

    @Test
    @DisplayName("test should depend on frontendTest")
    fun `test depend on frontendTest`() {
        runDryRunWithScripts("test", "frontendTest")
    }

    @Test
    @DisplayName("lint should depend on frontendLint")
    fun `lint depend on frontendLint`() {
        runDryRunWithScripts("check", "frontendLint")
    }

    @Test
    @DisplayName("frontendTest is skipped when no test script")
    fun `frontendTest skipped when script missing`() {
        builder = TestProjectBuilder
            .create()
            .withVersionCatalog()
            .withSettingsGradle()
            .withBuildGradle(basicPluginConfiguration())
            .withSettingsGradle()
            .withPackageJson(includeTest = false)
            .withJavaScriptSource()

        val result = builder.runGradle("test", "-m")

        result.output shouldContain ":frontendTest SKIPPED"
    }

    private fun runDryRunWithScripts(
        targetTask: String,
        expectedFrontendTask: String,
    ) {
        builder = TestProjectBuilder
            .create()
            .withVersionCatalog()
            .withSettingsGradle()
            .withBuildGradle(basicPluginConfiguration())
            .withPackageJson()
            .withResourceFile()
            .withJavaScriptSource()

        val result = builder.runGradle(targetTask, "-m")

        result.output shouldContain ":$expectedFrontendTask SKIPPED"
    }

}
