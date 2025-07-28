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
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import java.net.URI
import javax.inject.Inject

public open class LicenseExtension
    @Inject
    constructor(
        objects: ObjectFactory,
    ) {
        public val name: Property<String> = objects.property<String>().convention("Apache License Version 2.0")
        public val url: Property<URI> =
            objects.property<URI>().convention(URI("https://www.apache.org/licenses/LICENSE-2.0.txt"))
        public val distribution: Property<String> = objects.property<String>().convention("repo")
        public val comments: Property<String> = objects.property<String>().convention("Apache License Version 2.0")
    }

public open class LicensesExtension
    @Inject
    constructor(
        private val objects: ObjectFactory,
        private val licensesList: ListProperty<LicenseExtension>,
    ) {
        public fun license(action: LicenseExtension.() -> Unit) {
            licensesList.add(objects.newInstance<LicenseExtension>().apply(action))
        }
    }
