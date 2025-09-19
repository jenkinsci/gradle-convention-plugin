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
package io.github.aaravmahajanofficial.extensions

import io.github.aaravmahajanofficial.constants.ConfigurationConstants
import io.github.aaravmahajanofficial.extensions.bom.BomExtension
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension
import io.github.aaravmahajanofficial.utils.gradleProperty
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.Action
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.file.ProjectLayout
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
    layout: ProjectLayout,
    libs: VersionCatalog,
) {
    private val projectName = layout.projectDirectory.asFile.name

    public val jenkinsVersion: Property<String> =
        objects.property<String>().convention(
            gradleProperty(
                providers,
                ConfigurationConstants.Plugin.JENKINS_VERSION,
            ).orElse(versionFromCatalogOrFail(libs, "jenkins-core")),
        )

    public val artifactId: Property<String> =
        objects.property<String>().convention(projectName.removePrefix("jenkins-").removeSuffix("-plugin"))

    public val displayName: Property<String> =
        objects.property<String>().convention(
            artifactId.map { id ->
                id.split("-").joinToString(" ") { part -> part.replaceFirstChar { it.titlecase() } }
            },
        )

    public val gitHub: Property<URI> =
        objects.property<URI>().convention(
            artifactId.map { id -> URI.create("https://github.com/jenkinsci/$id-plugin") },
        )

    public val homePage: Property<URI> = objects.property<URI>().convention(gitHub)

    public val sandboxed: Property<Boolean> = objects.property<Boolean>().convention(false)

    public val usePluginFirstClassLoader: Property<Boolean> = objects.property<Boolean>().convention(true)

    public val maskedClassesFromCore: SetProperty<String> = objects.setProperty<String>().convention(emptySet())

    public val testJvmArguments: ListProperty<String> =
        objects
            .listProperty<String>()
            .convention(
                gradleProperty(providers, ConfigurationConstants.Plugin.TEST_JVM_ARGS) {
                    it.split(",").map(String::trim)
                }.orElse(
                    listOf(
                        "--add-opens=java.base/java.lang=ALL-UNNAMED",
                        "--add-opens=java.base/java.io=ALL-UNNAMED",
                        "--add-opens=java.base/java.util=ALL-UNNAMED",
                    ),
                ),
            )

    // Groovy DSL setter methods
    public fun jenkinsVersions(value: String): Unit = jenkinsVersion.set(value)

    public fun artifactId(value: String): Unit = artifactId.set(value)

    public fun displayName(value: String): Unit = displayName.set(value)

    public fun gitHub(value: URI): Unit = gitHub.set(value)

    public fun homePage(value: URI): Unit = homePage.set(value)

    public fun sandboxed(value: Boolean): Unit = sandboxed.set(value)

    public fun usePluginFirstClassLoader(value: Boolean): Unit = usePluginFirstClassLoader.set(value)

    public fun maskedClassesFromCore(vararg values: String): Unit = maskedClassesFromCore.set(values.toSet())

    public fun maskedClassesFromCore(values: Collection<String>): Unit = maskedClassesFromCore.set(values.toSet())

    public fun testJvmArguments(vararg values: String): Unit = testJvmArguments.set(values.toList())

    public fun testJvmArguments(values: Collection<String>): Unit = testJvmArguments.set(values.toList())

    internal val pluginDevelopers: ListProperty<DeveloperExtension> =
        objects.listProperty<DeveloperExtension>()

    internal val pluginLicenses: ListProperty<LicenseExtension> =
        objects.listProperty<LicenseExtension>().apply {
            add(objects.newInstance<LicenseExtension>())
        }

    public val developersExtension: DevelopersExtension = objects.newInstance<DevelopersExtension>(pluginDevelopers)
    public val licensesExtension: LicensesExtension = objects.newInstance<LicensesExtension>(pluginLicenses)
    public val bom: BomExtension = objects.newInstance<BomExtension>(libs)
    public val quality: QualityExtension = objects.newInstance<QualityExtension>()
    public val frontend: FrontendExtension = objects.newInstance<FrontendExtension>(libs)

    public fun developers(action: Action<DevelopersExtension>) {
        pluginDevelopers.set(emptyList())
        action.execute(developersExtension)
    }

    public fun licenses(action: Action<LicensesExtension>) {
        pluginLicenses.set(emptyList())
        action.execute(licensesExtension)
    }

    public fun bom(action: Action<BomExtension>) {
        action.execute(bom)
    }

    public fun quality(action: Action<QualityExtension>) {
        action.execute(quality)
    }

    public fun frontend(action: Action<FrontendExtension>) {
        action.execute(frontend)
    }
}
