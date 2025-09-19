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
package io.github.aaravmahajanofficial.extensions.quality

import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.OWASP_ENABLED
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension.Companion.DEFAULT_OWASP_THRESHOLD
import io.github.aaravmahajanofficial.utils.gradleProperty
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public open class OwaspDepCheckExtension
@Inject
constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
    layout: ProjectLayout,
) {
    public val enabled: Property<Boolean> =
        objects.property<Boolean>().convention(
            gradleProperty(providers, OWASP_ENABLED, String::toBoolean).orElse(false),
        )
    public val failOnCvss: Property<Float> =
        objects.property<Float>().convention(DEFAULT_OWASP_THRESHOLD)
    public val formats: ListProperty<String> =
        objects.listProperty<String>().convention(
            setOf("XML", "HTML", "SARIF"),
        )
    public val dataDirectory: DirectoryProperty =
        objects.directoryProperty().convention(
            layout.projectDirectory.dir(".gradle/dependency-check-data"),
        )
    public val outputDirectory: DirectoryProperty =
        objects.directoryProperty().convention(
            layout.buildDirectory.dir("reports/dependency-check"),
        )
    public val suppressionFiles: ListProperty<RegularFile> =
        objects
            .listProperty<RegularFile>()
            .convention(
                emptyList(),
            )
    public val scanConfigurations: ListProperty<String> =
        objects.listProperty<String>().convention(
            listOf("runtimeClasspath", "compileClasspath"),
        )
}
