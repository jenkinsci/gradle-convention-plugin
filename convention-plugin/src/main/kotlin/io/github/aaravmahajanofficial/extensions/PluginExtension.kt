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

package io.github.aaravmahajanofficial.extensions

import io.github.aaravmahajanofficial.constants.ConfigurationConstants
import io.github.aaravmahajanofficial.constants.UrlConstants
import io.github.aaravmahajanofficial.utils.gradleProperty
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
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
        providers: ProviderFactory,
        projectName: String,
        libs: VersionCatalog,
    ) {
        public val bom: BomExtension by lazy { objects.newInstance<BomExtension>(libs) }
        public val quality: QualityExtension by lazy { objects.newInstance<QualityExtension>(libs) }

        public fun bom(action: BomExtension.() -> Unit): BomExtension = bom.apply(action)

        public fun quality(action: QualityExtension.() -> Unit): QualityExtension = quality.apply(action)

        public val jenkinsVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.JENKINS_VERSION,
                ).orElse(versionFromCatalogOrFail(libs, "jenkins-core")),
            )

        public val artifactId: Property<String> =
            objects.property<String>().convention(
                gradleProperty(providers, ConfigurationConstants.ARTIFACT_ID).orElse(
                    projectName.removePrefix("jenkins-").removeSuffix("-plugin"),
                ),
            )

        public val homePage: Property<URI> =
            objects.property<URI>().convention(URI.create("https://github.com/jenkinsci/${artifactId.get()}"))

        public val sandboxed: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(providers, ConfigurationConstants.SANDBOXED, String::toBoolean).orElse(false),
            )

        public val usePluginFirstClassLoader: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.USE_PLUGIN_FIRST_CLASS_LOADER,
                    String::toBoolean,
                ).orElse(true),
            )

        public val maskedClassesFromCore: SetProperty<String> = objects.setProperty<String>().convention(emptySet())

        public val minimumJenkinsCoreVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.MINIMUM_JENKINS_VERSION,
                ).orElse(versionFromCatalogOrFail(libs, "jenkins-core")),
            )

        public val gitHub: Property<URI> =
            objects.property<URI>().convention(URI.create("https://github.com/jenkinsci/${artifactId.get()}"))

        public val scmTag: Property<String> = objects.property<String>().convention("HEAD")

        public val generateTests: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.GENERATE_TESTS,
                    String::toBoolean,
                ).orElse(false),
            )

        public val generatedTestClassName: Property<String> = objects.property<String>().convention("InjectedTest")

        public val extension: Property<String> = objects.property<String>().convention("hpi")

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

        public val pluginDevelopers: ListProperty<DeveloperExtension> = objects.listProperty<DeveloperExtension>()

        public val pluginLicenses: ListProperty<LicenseExtension> = objects.listProperty<LicenseExtension>()

        public fun developers(configure: DevelopersExtension.() -> Unit) {
            DevelopersExtension(objects, pluginDevelopers).apply(configure)
        }

        public fun licenses(configure: LicensesExtension.() -> Unit) {
            LicensesExtension(objects, pluginLicenses).apply(configure)
        }
    }
