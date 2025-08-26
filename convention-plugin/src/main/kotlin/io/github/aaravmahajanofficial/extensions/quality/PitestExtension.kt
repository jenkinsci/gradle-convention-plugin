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

import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.PITEST_ENABLED
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.PITEST_MUTATION_THRESHOLD
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension.Companion.DEFAULT_MUTATION_THRESHOLD
import io.github.aaravmahajanofficial.extensions.quality.QualityExtension.Companion.DEFAULT_THREADS
import io.github.aaravmahajanofficial.utils.gradleProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import javax.inject.Inject

public open class PitestExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(providers, PITEST_ENABLED, String::toBoolean).orElse(false),
            )
        public val threads: Property<Int> = objects.property<Int>().convention(DEFAULT_THREADS)
        public val targetClasses: ListProperty<String> = objects.listProperty<String>().convention(listOf("*"))
        public val excludedClasses: ListProperty<String> =
            objects.listProperty<String>().convention(listOf("*Test*"))
        public val mutationThreshold: Property<Int> =
            objects.property<Int>().convention(
                gradleProperty(providers, PITEST_MUTATION_THRESHOLD, String::toInt)
                    .orElse(DEFAULT_MUTATION_THRESHOLD),
            )
        public val outputFormats: ListProperty<String> =
            objects.listProperty<String>().convention(
                listOf("XML", "HTML"),
            )
        public val mutators: SetProperty<String> = objects.setProperty<String>().convention(setOf("DEFAULT"))
    }
