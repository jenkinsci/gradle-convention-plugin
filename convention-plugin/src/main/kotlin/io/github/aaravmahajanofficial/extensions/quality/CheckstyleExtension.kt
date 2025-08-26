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

import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.CHECKSTYLE_ENABLED
import io.github.aaravmahajanofficial.utils.gradleProperty
import io.github.aaravmahajanofficial.utils.versionFromCatalogOrFail
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public open class CheckstyleExtension
    @Inject
    constructor(
        libs: VersionCatalog,
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(providers, CHECKSTYLE_ENABLED, String::toBoolean).orElse(true),
            )
        public val toolVersion: Property<String> =
            objects.property<String>().convention(
                versionFromCatalogOrFail(libs, "checkstyle"),
            )
        public val failOnViolation: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val source: Property<String> = objects.property<String>().convention("src")
        public val include: ListProperty<String> = objects.listProperty<String>().convention(listOf("**/*.java"))
        public val exclude: ListProperty<String> = objects.listProperty<String>().convention(excludeList)
    }
