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
package io.github.aaravmahajanofficial.internal

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

public class TestingConventionManager(
    private val project: Project,
) {
    private val libs = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    public fun configure() {
        project.plugins.withId("java") {
            configureTestTasks()
            addBaseTestingDependencies()
        }

        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            addKotlinTestingDependencies()
        }

        project.plugins.withId("groovy") {
            addGroovyTestingDependencies()
        }
    }

    private fun configureTestTasks() {
        project.tasks.withType<Test> {
            useJUnitPlatform()

            forkEvery = 1L

            testLogging { logging ->
                logging.events(
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.FAILED,
                    TestLogEvent.STANDARD_ERROR,
                )

                logging.showExceptions = true
                logging.showCauses = true
                logging.showStackTraces = true
                logging.exceptionFormat = TestExceptionFormat.FULL
            }

            outputs.upToDateWhen { false }

            maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

            jvmArgs(
                "-Xms768M",
                "-Xmx768M",
                "-XX:+HeapDumpOnOutOfMemoryError",
                "-XX:+TieredCompilation",
                "-XX:TieredStopAtLevel=1",
            )

            systemProperty("file.encoding", "UTF-8")
            systemProperty("java.awt.headless", "true")
            systemProperty(
                "java.io.tmpdir",
                project.layout.buildDirectory
                    .dir("tmp")
                    .get()
                    .asFile.absolutePath,
            )
        }
    }

    private fun addBaseTestingDependencies() {
        project.dependencies {
            add("testImplementation", libs.findLibrary("junit-jupiter").get())
            add("testImplementation", libs.findLibrary("assertj-core").get())
            add("testImplementation", libs.findLibrary("mockito-core").get())
            add("testImplementation", libs.findLibrary("mockito-junit-jupiter").get())
        }
    }

    private fun addKotlinTestingDependencies() {
        project.dependencies {
            add("testImplementation", libs.findLibrary("kotest-assertions-core").get())
            add("testImplementation", libs.findLibrary("mockk").get())
        }
    }

    private fun addGroovyTestingDependencies() {
        project.dependencies {
            add("testImplementation", libs.findLibrary("spock-core").get())
        }
    }
}
