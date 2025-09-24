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
    private val ext: BomExtension,
) {
    public fun configure() {
        project.dependencies {
            configurePredefined()
            configureCustom()
        }
    }

    private fun DependencyHandler.configurePredefined() {
        listOf(
            Triple(ext.jenkins.enabled, ext.jenkins.coordinates, ext.jenkins.testOnly),
            Triple(ext.jackson.enabled, ext.jackson.coordinates, ext.jackson.testOnly),
            Triple(ext.spring.enabled, ext.spring.coordinates, ext.spring.testOnly),
            Triple(ext.jetty.enabled, ext.jetty.coordinates, ext.jetty.testOnly),
            Triple(ext.netty.enabled, ext.netty.coordinates, ext.netty.testOnly),
            Triple(ext.slf4j.enabled, ext.slf4j.coordinates, ext.slf4j.testOnly),
            Triple(ext.guava.enabled, ext.guava.coordinates, ext.guava.testOnly),
            Triple(ext.log4j.enabled, ext.log4j.coordinates, ext.log4j.testOnly),
            Triple(ext.vertx.enabled, ext.vertx.coordinates, ext.vertx.testOnly),
            Triple(ext.junit.enabled, ext.junit.coordinates, ext.junit.testOnly),
            Triple(ext.mockito.enabled, ext.mockito.coordinates, ext.mockito.testOnly),
            Triple(ext.testContainers.enabled, ext.testContainers.coordinates, ext.testContainers.testOnly),
        ).forEach { (enabled, coordinates, testOnly) -> applyIfEnabled(enabled, coordinates, testOnly) }
    }

    private fun DependencyHandler.configureCustom() {
        ext.customBoms.configureEach { bom ->
            val coordinates = bom.coordinates.orNull
            val version = bom.version.orNull

            require(!coordinates.isNullOrBlank()) { "Missing coordinates for BOM '${bom.name}'." }

            require(!version.isNullOrBlank()) { "Missing version for BOM '${bom.name}'." }

            val dep = platform("$coordinates:$version")

            if (!bom.testOnly.getOrElse(false)) {
                add("implementation", dep)
            }

            add("testImplementation", dep)
        }
    }

    private fun DependencyHandler.applyIfEnabled(
        enabled: Property<Boolean>,
        coordinates: Provider<MinimalExternalModuleDependency>,
        testOnly: Property<Boolean>,
    ) {
        if (!enabled.getOrElse(true)) return

        val dep = platform(coordinates.get())

        if (!testOnly.getOrElse(false)) {
            add("implementation", dep)
        }
        add("testImplementation", dep)
    }
}
