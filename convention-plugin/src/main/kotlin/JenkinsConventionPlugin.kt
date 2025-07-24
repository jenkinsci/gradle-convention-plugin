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
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import utils.GradleVersionUtils

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
                    project.provider { project.description },
                    libs,
                )

            JavaConventionManager(project).configure()
            KotlinConventionManager(project, libs).configure()
            GroovyConventionManager(project).configure()

            JpiPluginAdapter(project, pluginExtension).applyAndConfigure()

            project.afterEvaluate {
                try {
                    BomManager(project, pluginExtension.bom).configure()
                    QualityManager(project, pluginExtension.quality).apply()
                } catch (e: IllegalStateException) {
                    error("Failed to configure Jenkins convention plugin: ${e.message}")
                }
            }

            project.dependencies {
                add("testImplementation", "org.junit.jupiter:junit-jupiter-api:5.9.3")
                add("testImplementation", "org.junit.jupiter:junit-jupiter-params:5.9.3")
                add("testRuntimeOnly", "org.junit.jupiter:junit-jupiter-engine:5.9.3")
                add("testRuntimeOnly", "org.junit.platform:junit-platform-launcher:1.9.3")

                add("testImplementation", "org.jenkins-ci.plugins:pipeline-utility-steps")
                add("testImplementation", "org.jenkins-ci.plugins.workflow:workflow-job")
                add("testImplementation", "org.jenkins-ci.plugins.workflow:workflow-cps")
                add("testImplementation", "org.jenkins-ci.plugins.workflow:workflow-basic-steps")
                add("implementation", "org.eclipse.jgit:org.eclipse.jgit:7.2.1.202505142326-r")
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
