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
package io.github.aaravmahajanofficial.extensions.bom

import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Bom.VERTX_BOM
import io.github.aaravmahajanofficial.utils.gradleProperty
import io.github.aaravmahajanofficial.utils.libraryFromCatalog
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public open class VertxBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(providers, VERTX_BOM, String::toBoolean).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "vertx-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)

        // Groovy DSL setter methods
        public fun enabled(value: Boolean): Unit = enabled.set(value)

        public fun testOnly(value: Boolean): Unit = testOnly.set(value)
    }
