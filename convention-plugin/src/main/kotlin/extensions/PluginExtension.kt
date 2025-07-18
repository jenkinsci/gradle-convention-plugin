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
@file:Suppress("ktlint:standard:no-wildcard-imports")

package extensions

import DeveloperExtension
import DevelopersExtension
import constants.ConfigurationConstants
import constants.UrlConstants
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.*
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import java.net.URI
import javax.inject.Inject

public open class PluginExtension
    @Inject
    constructor(
        private val objects: ObjectFactory,
        private val providers: ProviderFactory,
        projectName: String,
        projectDescription: Provider<String>,
        libs: VersionCatalog,
    ) {
        public val bomExtension: BomExtension = objects.newInstance<BomExtension>(libs)

        public val qualityExtension: QualityExtension = objects.newInstance<QualityExtension>(libs)

        public fun bom(action: BomExtension.() -> Unit) {
            bomExtension.apply(action)
        }

        public fun quality(action: QualityExtension.() -> Unit) {
            qualityExtension.apply(action)
        }

        public val jenkinsVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    ConfigurationConstants.JENKINS_VERSION,
                ).orElse(libs.findVersion("jenkins-core").get().requiredVersion),
            )

        public val artifactId: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.PLUGIN_ID).orElse(
                    projectName.removePrefix("jenkins-").removeSuffix("-plugin"),
                ),
            )

        public val pluginId: Property<String> = objects.property<String>().convention(artifactId)

        public val groupId: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    ConfigurationConstants.GROUP_ID,
                ).orElse("org.jenkins-ci.plugins"),
            )

        public val humanReadableName: Property<String> =
            objects.property<String>().convention(
                projectDescription.orElse(projectName),
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
                ).orElse(true),
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

        public val pluginLabels: ListProperty<String> = objects.listProperty<String>().convention(emptySet())

//        public val pluginBundles: BundleExtension = objects.newInstance<BundleExtension>()
//
//        public fun bundles(action: BundleExtension.() -> Unit) {
//            pluginBundles.apply(action)
//        }

        public val pluginDevelopers: ListProperty<DeveloperExtension> =
            objects
                .listProperty<DeveloperExtension>()

        public val pluginLicenses: ListProperty<LicenseExtension> = objects.listProperty<LicenseExtension>()

        public fun developers(configure: DevelopersExtension.() -> Unit) {
            DevelopersExtension(objects, pluginDevelopers).apply(configure)
        }

        public fun licenses(configure: LicensesExtension.() -> Unit) {
            LicensesExtension(objects, pluginLicenses).apply(configure)
        }

        private fun <T : Any> gradleProperty(
            key: String,
            converter: (String) -> T,
        ): Provider<T> = providers.gradleProperty(key).map(converter)

        private fun gradleProperty(key: String) = providers.gradleProperty(key)

        public fun validate() {
            require(githubUrl.isPresent && githubUrl.toString().isNotBlank()) { "githubUrl must be set" }
            require(pluginDevelopers.getOrElse(emptyList()).isNotEmpty()) { "At least one developer must be specified" }
        }
    }
