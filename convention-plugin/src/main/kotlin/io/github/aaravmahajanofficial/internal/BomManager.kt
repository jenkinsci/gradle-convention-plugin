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

import io.github.aaravmahajanofficial.extensions.bom.BomExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.dependencies

public class BomManager(
    private val project: Project,
    private val bomExtension: BomExtension,
) {
    public fun configure() {
        project.dependencies {
            configureCommonBoms()
            configureCustomBoms()
        }
    }

    private fun DependencyHandler.configureCommonBoms() {
        applyBomIfEnabled(
            bomExtension.jenkins.enabled,
            bomExtension.jenkins.coordinates,
            bomExtension.jenkins.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.groovy.enabled,
            bomExtension.groovy.coordinates,
            bomExtension.groovy.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.jackson.enabled,
            bomExtension.jackson.coordinates,
            bomExtension.jackson.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.spring.enabled,
            bomExtension.spring.coordinates,
            bomExtension.spring.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.jetty.enabled,
            bomExtension.jetty.coordinates,
            bomExtension.jetty.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.netty.enabled,
            bomExtension.netty.coordinates,
            bomExtension.netty.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.slf4j.enabled,
            bomExtension.slf4j.coordinates,
            bomExtension.slf4j.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.guava.enabled,
            bomExtension.guava.coordinates,
            bomExtension.guava.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.log4j.enabled,
            bomExtension.log4j.coordinates,
            bomExtension.log4j.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.vertx.enabled,
            bomExtension.vertx.coordinates,
            bomExtension.vertx.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.junit.enabled,
            bomExtension.junit.coordinates,
            bomExtension.junit.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.mockito.enabled,
            bomExtension.mockito.coordinates,
            bomExtension.mockito.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.testContainers.enabled,
            bomExtension.testContainers.coordinates,
            bomExtension.testContainers.testOnly,
        )
        applyBomIfEnabled(
            bomExtension.spock.enabled,
            bomExtension.spock.coordinates,
            bomExtension.spock.testOnly,
        )
    }

    private fun DependencyHandler.configureCustomBoms() {
        bomExtension.customBoms.all { bom ->
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
