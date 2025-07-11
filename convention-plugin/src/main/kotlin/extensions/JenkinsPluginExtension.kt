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
package extensions

import constants.ConfigurationConstants
import constants.UrlConstants
import model.JenkinsPluginDependency
import model.JenkinsPluginDeveloper
import model.JenkinsPluginLicense
import org.gradle.api.Project
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import java.net.URI
import javax.inject.Inject

public abstract class JenkinsPluginExtension
    @Inject
    constructor(
        private val project: Project,
    ) {
        private val objects: ObjectFactory = project.objects

        private fun <T : Any> gradleProperty(
            key: String,
            converter: (String) -> T,
        ): Provider<T> = project.providers.gradleProperty(key).map(converter)

        private fun gradleProperty(key: String) = project.providers.gradleProperty(key)

        public val jenkinsVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    ConfigurationConstants.JENKINS_VERSION,
                ).orElse("2.504.3"),
            )

        public val pluginId: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.PLUGIN_ID).orElse(
                    project.name.removePrefix("jenkins-").removeSuffix("-plugin"),
                ),
            )

        public val artifactId: Property<String> = objects.property<String>().convention(pluginId)

        public val groupId: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    ConfigurationConstants.GROUP_ID,
                ).orElse("org.jenkins-ci.plugins"),
            )

        public val humanReadableName: Property<String> =
            objects.property<String>().convention(
                project.provider {
                    project.description ?: project.name
                },
            )

        public val homePage: Property<URI> = objects.property<URI>()

        public val sandboxed: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(ConfigurationConstants.SANDBOXED, String::toBoolean).orElse(false),
            )

        public val usePluginFirstClassLoader: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_PLUGIN_FIRST_CLASS_LOADER,
                    String::toBoolean,
                ).orElse(false),
            )

        public val maskedClassesFromCore: SetProperty<String> = objects.setProperty<String>().convention(emptySet())

        public val minimumJenkinsCoreVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    ConfigurationConstants.MINIMUM_JENKINS_VERSION,
                ).orElse("2.504.3"),
            )

        public val description: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    ConfigurationConstants.DESCRIPTION,
                ).orElse("A Jenkins Plugin"),
            )

        public val githubUrl: Property<URI> = objects.property<URI>()

        public val pluginDevelopers: ListProperty<JenkinsPluginDeveloper> =
            objects
                .listProperty<JenkinsPluginDeveloper>()

        public val pluginLicenses: ListProperty<JenkinsPluginLicense> = objects.listProperty<JenkinsPluginLicense>()

        public val pluginType: Property<PluginType> = objects.property<PluginType>().convention(PluginType.MISC)

        public val generateTests: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.GENERATE_TESTS,
                    String::toBoolean,
                ).orElse(false),
            )

        public val generatedTestClassName: Property<String> = objects.property<String>().convention("InjectedTest")

        public val extension: Property<String> = objects.property<String>().convention(".hpi")

        public val scmTag: Property<String> = objects.property<String>().convention("HEAD")

        public val requireEscapeByDefaultInJelly: Property<Boolean> = objects.property<Boolean>().convention(true)

        public val incrementalsRepoUrl: Property<String> =
            objects.property<String>().convention(UrlConstants.JENKINS_INCREMENTALS_REPO_URL)

        public val testJvmArguments: ListProperty<String> =
            objects
                .listProperty<String>()
                .convention(
                    listOf(
                        "--add-opens=java.base/java.lang=ALL-UNNAMED",
                        "--add-opens=java.base/java.io=ALL-UNNAMED",
                        "--add-opens=java.base/java.util=ALL-UNNAMED",
                    ),
                )

        public val pluginDependencies: ListProperty<JenkinsPluginDependency> =
            objects.listProperty<JenkinsPluginDependency>()

        public val pluginLabels: ListProperty<String> = objects.listProperty<String>().convention(emptySet())

        public enum class PluginType {
            BUILD,
            SCM,
            NOTIFICATION,
            DEPLOYMENT,
            SECURITY,
            PIPELINE,
            TESTING,
            INTEGRATION,
            ADMINISTRATION,
            MISC,
        }

        public fun developer(configure: JenkinsPluginDeveloper.() -> Unit) {
            pluginDevelopers.add(objects.newInstance<JenkinsPluginDeveloper>().apply(configure))
        }

        public fun license(configure: JenkinsPluginLicense.() -> Unit) {
            pluginLicenses.add(objects.newInstance<JenkinsPluginLicense>().apply(configure))
        }

        public fun dependency(
            pluginId: String,
            action: JenkinsPluginDependency.() -> Unit,
        ) {
            val dependency =
                objects.newInstance<JenkinsPluginDependency>().apply {
                    this.pluginId.set(pluginId)
                }
            action(dependency)
            pluginDependencies.add(dependency)
        }

        public fun dependency(configure: JenkinsPluginDependency.() -> Unit) {
            pluginDependencies.add(objects.newInstance<JenkinsPluginDependency>().apply(configure))
        }
    }
