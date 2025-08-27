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

import org.gradle.api.Action
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
        public val id: Property<String> = objects.property<String>()
        public val name: Property<String> = objects.property<String>()
        public val email: Property<String> = objects.property<String>()
        public val website: Property<URI> = objects.property<URI>()
        public val organization: Property<String> = objects.property<String>().convention("Jenkins Community")
        public val organizationUrl: Property<URI> =
            objects
                .property<URI>()
                .convention(URI.create("https://github.com/jenkinsci"))
        public val roles: SetProperty<String> =
            objects.setProperty<String>().convention(setOf("developer", "contributor"))
        public val timezone: Property<String> = objects.property<String>().convention("UTC")

        // Groovy DSL setter methods
        public fun id(value: String): Unit = id.set(value)

        public fun name(value: String): Unit = name.set(value)

        public fun email(value: String): Unit = email.set(value)

        public fun website(value: URI): Unit = website.set(value)

        public fun organization(value: String): Unit = organization.set(value)

        public fun organizationUrl(value: URI): Unit = organizationUrl.set(value)

        public fun timeZone(value: String): Unit = timezone.set(value)

        public fun roles(vararg values: String): Unit = roles.set(values.toSet())

        public fun roles(values: Collection<String>): Unit = roles.set(values.toSet())

        internal fun validate() {
            require(!id.orNull.isNullOrBlank()) { "Developer 'id' is required." }
            require(!name.orNull.isNullOrBlank()) { "Developer 'name' is required." }
            require(!email.orNull.isNullOrBlank()) { "Developer 'email' is required." }
        }
    }

public open class DevelopersExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        private val developersList: ListProperty<DeveloperExtension>,
    ) {
        internal val developer: DeveloperExtension = objects.newInstance<DeveloperExtension>()

        public fun developer(action: Action<DeveloperExtension>) {
            action.execute(developer)
            developersList.add(developer)
        }
    }
