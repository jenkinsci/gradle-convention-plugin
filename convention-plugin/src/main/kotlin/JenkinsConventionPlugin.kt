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
import constants.PluginMetadata
import extensions.BomExtension
import extensions.JenkinsPluginExtension
import extensions.QualityExtension
import internal.BomManager
import internal.JavaConventionManager
import internal.JpiPluginAdapter
import internal.KotlinConventionManager
import internal.LanguagePluginValidator
import internal.QualityManager
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.problems.Problems
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import utils.GradleVersionUtils
import javax.inject.Inject

@SuppressWarnings("INCUBATING")
public abstract class JenkinsConventionPlugin
    @Inject
    constructor(
        private val problems: Problems,
    ) : Plugin<Project> {
        override fun apply(project: Project) {
            GradleVersionUtils.verifyGradleVersion()
            val libs: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

            val pluginExtension =
                project.extensions.create<JenkinsPluginExtension>(
                    PluginMetadata.EXTENSION_NAME,
                    project,
                )
            val bomExtension = project.extensions.create<BomExtension>("bom", project.objects, libs)
            val qualityExtension = project.extensions.create<QualityExtension>("quality", project, libs)

            LanguagePluginValidator(project, problems).validate()
            JpiPluginAdapter(project, pluginExtension).applyAndConfigure()
            BomManager(project, bomExtension).configure()
            QualityManager(project, qualityExtension).apply()
            JavaConventionManager(project).configure()
            KotlinConventionManager(project, libs).configure()
        }
    }
