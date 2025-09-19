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

import com.github.spotbugs.snom.Confidence
import com.github.spotbugs.snom.Effort
import io.github.aaravmahajanofficial.constants.ConfigurationConstants.Quality.SPOTBUGS_ENABLED
import io.github.aaravmahajanofficial.utils.gradleProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.listProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public open class SpotbugsExtension
@Inject
constructor(
    objects: ObjectFactory,
    providers: ProviderFactory,
) {
    public val enabled: Property<Boolean> =
        objects.property<Boolean>().convention(
            gradleProperty(providers, SPOTBUGS_ENABLED, String::toBoolean).orElse(true),
        )
    public val effortLevel: Property<Effort> = objects.property<Effort>().convention(Effort.MAX)
    public val reportLevel: Property<Confidence> = objects.property<Confidence>().convention(Confidence.LOW)
    public val failOnError: Property<Boolean> = objects.property<Boolean>().convention(true)
    public val omitVisitors: ListProperty<String> = objects.listProperty<String>().convention(emptyList())

    // Groovy DSL setter methods
    public fun enabled(value: Boolean): Unit = enabled.set(value)

    public fun effortLevel(value: Effort): Unit = effortLevel.set(value)

    public fun reportLevel(value: Confidence): Unit = reportLevel.set(value)

    public fun failOnError(value: Boolean): Unit = failOnError.set(value)

    public fun omitVisitors(values: Collection<String>): Unit = omitVisitors.set(values.toList())

    public fun omitVisitors(vararg values: String): Unit = omitVisitors.set(values.toList())
}
