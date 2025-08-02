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

import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import org.gradle.kotlin.dsl.setProperty
import java.net.URI
import javax.inject.Inject

public open class DeveloperExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val id: Property<String> = objects.property<String>().convention("")
        public val name: Property<String> = objects.property<String>().convention("")
        public val email: Property<String> = objects.property<String>().convention("")
        public val website: Property<URI> = objects.property<URI>().convention(URI("https://github.com"))
        public val organization: Property<String> = objects.property<String>().convention("")
        public val organizationUrl: Property<URI> = objects.property<URI>().convention(URI("https://github.com"))
        public val roles: SetProperty<String> = objects.setProperty<String>().convention(setOf("dev"))
        public val timezone: Property<String> = objects.property<String>().convention("UTC")
    }

public open class DevelopersExtension
    @Inject
    constructor(
        private val objects: ObjectFactory,
        private val developersList: ListProperty<DeveloperExtension>,
    ) {
        public fun developer(action: DeveloperExtension.() -> Unit) {
            developersList.add(objects.newInstance<DeveloperExtension>().apply(action))
        }
    }
