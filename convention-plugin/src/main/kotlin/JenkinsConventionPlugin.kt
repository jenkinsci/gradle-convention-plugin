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
@file:Suppress("UnstableApiUsage", "ktlint:standard:no-wildcard-imports")

import constants.PluginMetadata
import extensions.PluginExtension
import internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import utils.GradleVersionUtils
import utils.libs

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            GradleVersionUtils.verifyGradleVersion()

            val pluginExtension =
                extensions.create(
                    PluginMetadata.EXTENSION_NAME,
                    PluginExtension::class.java,
                    project.name,
                    project.provider { project.description },
                    libs,
                )

            JavaConventionManager(project).configure()
            project.plugins.withId("org.jetbrains.kotlin.jvm") {
                KotlinConventionManager(project, libs).configure()
            }
            project.plugins.withId("groovy") {
                GroovyConventionManager(project, libs).configure()
            }

            JpiPluginAdapter(project, pluginExtension).applyAndConfigure()

            project.afterEvaluate {
                try {
                    BomManager(project, pluginExtension).configure()
//                    BundleManager(project, pluginExtension).configure()
                    QualityManager(project, pluginExtension).apply()
                } catch (e: IllegalStateException) {
                    error("Failed to configure Jenkins convention plugin: ${e.message}")
                }
            }
        }
    }
}
