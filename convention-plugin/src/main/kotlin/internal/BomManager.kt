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
package internal

import extensions.PluginExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies

public class BomManager(
    private val project: Project,
    private val pluginExtension: PluginExtension,
) {
    public fun configure() {
        project.dependencies {
            configureCommonBoms()
            configureCustomBoms()
        }
    }

    private fun DependencyHandler.configureCommonBoms() {
        applyBomIfEnabled(
            pluginExtension.bomExtension.jenkins.enabled,
            pluginExtension.bomExtension.jenkins.coordinates,
            pluginExtension.bomExtension.jenkins.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.groovy.enabled,
            pluginExtension.bomExtension.groovy.coordinates,
            pluginExtension.bomExtension.groovy.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.jackson.enabled,
            pluginExtension.bomExtension.jackson.coordinates,
            pluginExtension.bomExtension.jackson.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.spring.enabled,
            pluginExtension.bomExtension.spring.coordinates,
            pluginExtension.bomExtension.spring.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.jetty.enabled,
            pluginExtension.bomExtension.jetty.coordinates,
            pluginExtension.bomExtension.jetty.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.netty.enabled,
            pluginExtension.bomExtension.netty.coordinates,
            pluginExtension.bomExtension.netty.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.slf4j.enabled,
            pluginExtension.bomExtension.slf4j.coordinates,
            pluginExtension.bomExtension.slf4j.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.guava.enabled,
            pluginExtension.bomExtension.guava.coordinates,
            pluginExtension.bomExtension.guava.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.log4j.enabled,
            pluginExtension.bomExtension.log4j.coordinates,
            pluginExtension.bomExtension.log4j.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.vertx.enabled,
            pluginExtension.bomExtension.vertx.coordinates,
            pluginExtension.bomExtension.vertx.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.junit.enabled,
            pluginExtension.bomExtension.junit.coordinates,
            pluginExtension.bomExtension.junit.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.mockito.enabled,
            pluginExtension.bomExtension.mockito.coordinates,
            pluginExtension.bomExtension.mockito.testOnly,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.testContainers.enabled,
            pluginExtension.bomExtension.testContainers.coordinates,
            pluginExtension.bomExtension.testContainers.testOnly,
        )
    }

    private fun DependencyHandler.configureCustomBoms() {
        pluginExtension.bomExtension.customBoms.all { bom ->
            val coordinates = bom.coordinates.orNull
            val version = bom.version.orNull

            require(!coordinates.isNullOrBlank()) {
                "Missing coordinates for BOM '${bom.name}'."
            }

            require(!version.isNullOrBlank()) {
                "Missing version for BOM '${bom.name}'."
            }

            val platformDependency = enforcedPlatform("$coordinates:$version")
            add("implementation", platformDependency)
            add("testImplementation", platformDependency)
        }
    }

    private fun DependencyHandler.applyBomIfEnabled(
        enabled: Property<Boolean>,
        coordinates: Provider<MinimalExternalModuleDependency>,
        testOnly: Property<Boolean>,
    ) {
        if (enabled.getOrElse(true)) {
            val platformDependency = enforcedPlatform(coordinates.get())
            if (!testOnly.getOrElse(false)) {
                add("implementation", platformDependency)
            }
            add("testImplementation", platformDependency)
        }
    }
}
