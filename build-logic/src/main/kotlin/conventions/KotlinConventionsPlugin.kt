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
package conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

private const val JAVA_VERSION = 17

public class KotlinConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("java-gradle-plugin")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")
            project.configureKotlin()
            project.configureCommonDependencies(libs)
        }
    }
}

private fun Project.configureKotlin() {
    configure<KotlinJvmProjectExtension> {
        jvmToolchain(JAVA_VERSION)

        explicitApi()

        compilerOptions {
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
            jvmTarget.set(JvmTarget.JVM_17)

            allWarningsAsErrors.set(true)
            progressiveMode.set(false)

            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all",
            )
        }
    }
}

private fun Project.configureCommonDependencies(libs: VersionCatalog) {
    dependencies {
        "implementation"(platform(libs.findLibrary("kotlin-bom").get()))
        "implementation"(libs.findLibrary("kotlin-stdlib").get())
        "implementation"(libs.findLibrary("kotlin-reflect").get())

        "compileOnly"(libs.findLibrary("jetbrains-annotations").get())
    }
}
