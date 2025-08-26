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

import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.JACOCO_ENABLED
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension.Companion.DEFAULT_CODE_COVERAGE_THRESHOLD
import io.github.aaravmahajanofficial.utils.gradleProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public open class JacocoExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(providers, JACOCO_ENABLED, String::toBoolean).orElse(true),
            )
        public val minimumCodeCoverage: Property<Double> =
            objects.property<Double>().convention(
                DEFAULT_CODE_COVERAGE_THRESHOLD,
            )
        public val excludes: ListProperty<String> = objects.listProperty<String>().convention(emptyList())

        // Groovy DSL setter methods
        public fun enabled(value: Boolean): Unit = enabled.set(value)

        public fun minimumCodeCoverage(value: Double): Unit = minimumCodeCoverage.set(value)

        public fun excludes(vararg values: String): Unit = excludes.set(values.toList())

        public fun excludes(values: Collection<String>): Unit = excludes.set(values.toList())
    }
