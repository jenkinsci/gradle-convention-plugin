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

import io.github.aaravmahajanofficial.utils.libsCatalog
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.testing.Test
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType

public class TestingConfig(
    private val project: Project,
) {
    private val libs = project.libsCatalog()

    public fun configure() {
        project.plugins.withType<JavaPlugin> {
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
        project.tasks.withType<Test>().configureEach { t ->
            t.useJUnitPlatform()

            t.forkEvery = 1L

            t.testLogging { logging ->
                logging.events(
                    TestLogEvent.PASSED,
                    TestLogEvent.SKIPPED,
                    TestLogEvent.FAILED,
                )

                logging.exceptionFormat = TestExceptionFormat.FULL
            }

            t.outputs.upToDateWhen { false }

            t.maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)

            t.jvmArgs(
                "-Xms768M",
                "-Xmx768M",
                "-XX:+HeapDumpOnOutOfMemoryError",
                "-XX:+TieredCompilation",
                "-XX:TieredStopAtLevel=1",
            )

            t.systemProperty("file.encoding", "UTF-8")
            t.systemProperty("java.awt.headless", "true")
            t.systemProperty(
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
            add("testRuntimeOnly", libs.findLibrary("junit-platform-launcher").get())
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
