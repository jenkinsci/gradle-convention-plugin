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
import extensions.BomExtension
import extensions.PluginExtension
import extensions.QualityExtension
import internal.*
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.newInstance
import utils.GradleVersionUtils
import utils.libs

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            GradleVersionUtils.verifyGradleVersion()

            val pluginExtension =
                objects.newInstance<PluginExtension>(project.name, project.provider { project.description })
            extensions.add(PluginMetadata.EXTENSION_NAME, pluginExtension)

            val bomExtension = objects.newInstance<BomExtension>(libs)
            extensions.add("bom", bomExtension)

            val qualityExtension = objects.newInstance<QualityExtension>(libs)
            extensions.add("quality", qualityExtension)

            JavaConventionManager(project).configure()
            KotlinConventionManager(project, libs).configure()
            GroovyConventionManager(project, libs).configure()
            JpiPluginAdapter(project, pluginExtension).applyAndConfigure()
            BomManager(project, bomExtension).configure()
            QualityManager(project, qualityExtension).apply()
        }
    }
}
