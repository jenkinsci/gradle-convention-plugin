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

import io.github.aaravmahajanofficial.constants.UrlConstants
import io.github.aaravmahajanofficial.extensions.PluginExtension
import org.eclipse.jgit.api.Git
import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.withType
import org.jenkinsci.gradle.plugins.jpi.JpiExtension
import org.jenkinsci.gradle.plugins.jpi.core.PluginDeveloper
import org.jenkinsci.gradle.plugins.jpi.core.PluginLicense
import org.jenkinsci.gradle.plugins.manifest.GenerateJenkinsManifestTask
import java.nio.file.Paths
import java.util.jar.Manifest

public class JpiPluginManager(
    private val project: Project,
    private val pluginExtension: PluginExtension,
) {
    private val jpiExtension: JpiExtension by lazy {
        project.extensions.getByType<JpiExtension>()
    }

    public fun applyAndConfigure() {
        project.pluginManager.apply("org.jenkins-ci.jpi")

        pluginExtension.pluginDevelopers.get().forEach { it.validate() }

        bridgeExtensionProperties()
        project.tasks.findByName("generateLicenseInfo")?.enabled = false

        configureManifestExtensions()
    }

    private fun bridgeExtensionProperties() =
        with(jpiExtension) {
            pluginId.convention(pluginExtension.artifactId)
            humanReadableName.convention(pluginExtension.displayName)
            homePage.convention(pluginExtension.homePage)
            jenkinsVersion.convention(pluginExtension.jenkinsVersion)
            minimumJenkinsCoreVersion.convention(jenkinsVersion)
            extension.convention("hpi")
            scmTag.convention("HEAD")
            gitHub.convention(pluginExtension.gitHub)
            sandboxed.convention(pluginExtension.sandboxed)
            usePluginFirstClassLoader.convention(pluginExtension.usePluginFirstClassLoader)
            maskedClassesFromCore.convention(pluginExtension.maskedClassesFromCore)
            incrementalsRepoUrl.convention(UrlConstants.JENKINS_INCREMENTALS_REPO_URL)
            testJvmArguments.convention(pluginExtension.testJvmArguments)
            configureRepositories = false

            pluginDevelopers.set(
                pluginExtension.pluginDevelopers.map { developers ->
                    developers.map { dev ->
                        project.objects.newInstance<PluginDeveloper>().apply {
                            id.set(dev.id)
                            name.set(dev.name)
                            email.set(dev.email)
                            url.set(
                                dev.website
                                    .orElse(gitHub.get())
                                    .get()
                                    .toString(),
                            )
                            organization.set(dev.organization)
                            organizationUrl.set(dev.organizationUrl.get().toString())
                            roles.set(dev.roles)
                            timezone.set(dev.timezone)
                        }
                    }
                },
            )

            pluginLicenses.set(
                pluginExtension.pluginLicenses.map { licenses ->
                    licenses.map { lic ->
                        project.objects.newInstance<PluginLicense>().apply {
                            name.set(lic.name)
                            url.set(
                                lic.url
                                    .orElse(gitHub.get())
                                    .get()
                                    .toString() + "/blob/master/LICENSE",
                            )
                            distribution.set(lic.distribution)
                            comments.set(lic.comments)
                        }
                    }
                },
            )
        }

    private fun getFullHashFromJpi(): String {
        val repoDir = Paths.get(project.rootDir.absolutePath).toFile()
        return try {
            Git.open(repoDir).use { git ->
                git.repository.resolve("HEAD").name
                    ?: error("Cannot resolve HEAD in repo: $repoDir")
            }
        } catch (e: IllegalStateException) {
            throw IllegalStateException("Failed to retrieve Git HEAD SHA in repo: $repoDir", e)
        }
    }

    private fun configureManifestExtensions() {
        project.tasks.withType<GenerateJenkinsManifestTask>().configureEach { task ->

            val additionalManifestFile = project.layout.buildDirectory.file("jenkins-manifests/additional.mf")
            task.upstreamManifests.from(additionalManifestFile)

            task.doFirst {
                val manifest =
                    Manifest().apply {
                        mainAttributes.putValue("Manifest-Version", "1.0")
                        mainAttributes.putValue("Implementation-Title", pluginExtension.artifactId.get())
                        mainAttributes.putValue("Implementation-Version", project.version.toString())
                        mainAttributes.putValue("Specification-Title", project.name)
                        mainAttributes.putValue("Specification-Version", project.version.toString())
                        mainAttributes.putValue("Artifact-Id", pluginExtension.artifactId.get())
                        mainAttributes.putValue("Hudson-Version", pluginExtension.jenkinsVersion.get())
                        mainAttributes.putValue("Plugin-ScmConnection", "scm:git:${pluginExtension.gitHub.get()}.git")
                        mainAttributes.putValue("Plugin-ScmUrl", pluginExtension.gitHub.get().toString())
                        mainAttributes.putValue("Build-Jdk-Spec", JavaVersion.current().toString())

                        val fullHash = getFullHashFromJpi()
                        mainAttributes.putValue("Implementation-Build", fullHash)
                        mainAttributes.putValue("Plugin-ScmTag", fullHash)

                        val license = pluginExtension.pluginLicenses.orNull?.firstOrNull()
                        license?.let {
                            it.name.orNull?.let { name -> mainAttributes.putValue("Plugin-License-Name", name) }
                            it.url.orNull?.let { url -> mainAttributes.putValue("Plugin-License-Url", url.toString()) }
                        }
                    }

                val file = additionalManifestFile.get().asFile
                file.parentFile.mkdirs()
                file.outputStream().use { stream ->
                    manifest.write(stream)
                }
            }
        }
    }
}
