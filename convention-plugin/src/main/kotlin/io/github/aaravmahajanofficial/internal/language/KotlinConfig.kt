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
package io.github.aaravmahajanofficial.internal.language

import io.github.aaravmahajanofficial.constants.PluginMetadata.JAVA_VERSION
import io.github.aaravmahajanofficial.utils.libsCatalog
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

public class KotlinConfig(
    private val project: Project,
) {
    private val libs = project.libsCatalog()
    private val kotlinVersion = KotlinVersion.fromVersion(libs.findVersion("kotlinLanguage").get().requiredVersion)

    public fun configure() {
        project.plugins.withId("org.jetbrains.kotlin.jvm") {
            project.configure<KotlinJvmProjectExtension> {
                jvmToolchain(JAVA_VERSION)
            }

            project.tasks.withType<KotlinCompile>().configureEach { t ->
                t.compilerOptions {
                    languageVersion.set(kotlinVersion)
                    apiVersion.set(kotlinVersion)
                    jvmTarget.set(JvmTarget.fromTarget(JAVA_VERSION.toString()))
                    allWarningsAsErrors.set(true)
                    progressiveMode.set(true)
                    optIn.add("kotlin.RequiresOptIn")
                    freeCompilerArgs.addAll(
                        "-Xjsr305=strict",
                    )
                }
            }

            project.dependencies {
                add("implementation", platform(libs.findLibrary("kotlin-bom").get()))
                add("compileOnly", libs.findLibrary("jetbrains-annotations").get())
            }
        }
    }
}
