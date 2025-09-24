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
package io.github.aaravmahajanofficial.internal

import de.skuzzle.restrictimports.gradle.RestrictImportsExtension
import de.skuzzle.restrictimports.gradle.RestrictImportsPlugin
import io.github.aaravmahajanofficial.extensions.PluginExtension
import org.gradle.api.Project

public class RestrictImportsConfig(
    private val project: Project,
    private val ext: PluginExtension,
) {
    public fun configure() {
        project.pluginManager.apply(RestrictImportsPlugin::class.java)

        if (ext.banJUnit4.get()) {
            project.extensions.configure(RestrictImportsExtension::class.java) { restrictImports ->
                restrictImports.reason.set("Please use JUnit 5 (JUnit Jupiter) instead of JUnit 4")
                restrictImports.bannedImports.set(listOf("org.junit.**"))
                restrictImports.allowedImports.set(listOf("org.junit.jupiter.**"))
            }
        }

        project.tasks.named("check").configure {
            it.dependsOn("restrictImports")
        }
    }
}
