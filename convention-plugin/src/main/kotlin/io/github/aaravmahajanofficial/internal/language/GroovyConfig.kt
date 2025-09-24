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
package io.github.aaravmahajanofficial.internal.language

import io.github.aaravmahajanofficial.utils.libsCatalog
import org.gradle.api.Project
import org.gradle.api.tasks.compile.GroovyCompile
import org.gradle.kotlin.dsl.withType

public class GroovyConfig(
    private val project: Project,
) {
    private val libs = project.libsCatalog()
    private val jvmTargetVersion = libs.findVersion("jvmTarget").get().requiredVersion

    public fun configure() {
        project.plugins.withId("groovy") {
            project.tasks.withType<GroovyCompile>().configureEach { t ->
                t.groovyOptions.encoding = "UTF-8"
                t.groovyOptions.optimizationOptions?.put("indy", true)
                t.options.release.set(jvmTargetVersion.toInt())
                t.options.compilerArgs.addAll(listOf("-parameters"))
            }
        }
    }
}
