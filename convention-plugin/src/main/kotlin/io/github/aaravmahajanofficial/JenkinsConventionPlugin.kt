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

import io.github.aaravmahajanofficial.constants.PluginMetadata
import io.github.aaravmahajanofficial.constants.UrlConstants.JENKINS_PUBLIC_REPO_URL
import io.github.aaravmahajanofficial.extensions.PluginExtension
import io.github.aaravmahajanofficial.internal.BomManager
import io.github.aaravmahajanofficial.internal.FrontendConfig
import io.github.aaravmahajanofficial.internal.JpiPluginConfig
import io.github.aaravmahajanofficial.internal.RestrictImportsConfig
import io.github.aaravmahajanofficial.internal.TestJarConfig
import io.github.aaravmahajanofficial.internal.TestingConfig
import io.github.aaravmahajanofficial.internal.language.GroovyConfig
import io.github.aaravmahajanofficial.internal.language.JavaConfig
import io.github.aaravmahajanofficial.internal.language.KotlinConfig
import io.github.aaravmahajanofficial.internal.quality.QualityManager
import io.github.aaravmahajanofficial.utils.GradleVersionUtils
import io.github.aaravmahajanofficial.utils.libsCatalog
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.repositories

public class JenkinsConventionPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            GradleVersionUtils.verifyGradleVersion()

            group = "io.jenkins.plugins"

            repositories {
                gradlePluginPortal()
                mavenCentral()
                maven {
                    it.name = "jenkinsPublic"
                    it.url = JENKINS_PUBLIC_REPO_URL
                }
                maven {
                    it.url = uri("https://repo.eclipse.org/content/groups/releases/")
                }
            }

            val libs = libsCatalog()
            val ext = extensions.create<PluginExtension>(PluginMetadata.EXTENSION_NAME, libs)

            JavaConfig(this).configure()
            GroovyConfig(this).configure()
            KotlinConfig(this).configure()

            JpiPluginConfig(this, ext).applyAndConfigure()

            project.afterEvaluate {
                try {
                    BomManager(this, ext.bom).configure()
                    TestingConfig(this, ext).configure()
                    QualityManager(this, ext.quality).apply()
                    RestrictImportsConfig(this, ext).configure()
                    FrontendConfig(this, ext.frontend).configure()
                    TestJarConfig(this, ext).configure()
                } catch (e: IllegalStateException) {
                    error("Failed to configure Jenkins convention plugin: ${e.message}")
                }
            }

            tasks.register("jenkinsConventionPluginInfo") { t ->
                group = "Help"
                description = "Prints the convention plugin configuration."

                val jenkinsVersion =
                    project.provider {
                        extensions.getByType<PluginExtension>().jenkinsVersion.orNull
                    }

                t.doLast {
                    println("jenkinsVersion: ${jenkinsVersion.get()}")
                }
            }
        }
    }
}
