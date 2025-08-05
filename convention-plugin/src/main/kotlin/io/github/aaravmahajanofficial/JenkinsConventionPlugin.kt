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
@file:Suppress("UnstableApiUsage", "ktlint:standard:no-wildcard-imports", "WildcardImport")

package io.github.aaravmahajanofficial

import io.github.aaravmahajanofficial.constants.PluginMetadata
import io.github.aaravmahajanofficial.extensions.PluginExtension
import io.github.aaravmahajanofficial.internal.*
import io.github.aaravmahajanofficial.utils.GradleVersionUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            GradleVersionUtils.verifyGradleVersion()

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            val pluginExtension =
                extensions.create(
                    PluginMetadata.EXTENSION_NAME,
                    PluginExtension::class.java,
                    project.name,
                    project.rootDir,
                    libs,
                )

            JavaConventionManager(project).configure()
            KotlinConventionManager(project, libs).configure()
            GroovyConventionManager(project).configure()

            JpiPluginManager(project, pluginExtension).applyAndConfigure()

            project.afterEvaluate {
                try {
                    BomManager(project, pluginExtension.bom).configure()
                    QualityManager(project, libs, pluginExtension.quality).apply()
                } catch (e: IllegalStateException) {
                    error("Failed to configure Jenkins convention plugin: ${e.message}")
                }
            }

            tasks.register("jenkinsConventionPluginInfo") { t ->
                group = "Help"
                description = "Prints the convention plugin configuration."

                t.doLast {
                    val extension = project.extensions.getByType<PluginExtension>()

                    println("artifactId: ${extension.artifactId.orNull}")
                }
            }
        }
    }
}
