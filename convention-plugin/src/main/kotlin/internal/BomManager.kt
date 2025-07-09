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

import extensions.BomExtension
import internal.BomManager.BOMCoordinates.JENKINS_BOM
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.dependencies

public class BomManager(
    private val project: Project,
    private val bomExtension: BomExtension,
) {
    public fun configure() {
        project.dependencies {
            configureCoreBom()
            configureCommonBoms()
            configureTestingBom()
            configureCustomBoms()
        }
    }

    private fun DependencyHandler.applyBomIfEnabled(
        enabled: Property<Boolean>,
        coordinates: String,
        version: Property<String>,
        testOnly: Boolean = false,
    ) {
        if (enabled.get()) {
            val platformDependency = platform("$coordinates:${version.get()}")
            if (!testOnly) {
                add("implementation", platformDependency)
            }
            add("testImplementation", platformDependency)
        }
    }

    private fun DependencyHandler.configureCoreBom() {
        applyBomIfEnabled(
            bomExtension.useCoreBom,
            BOMCoordinates.JENKINS_BOM,
            bomExtension.bomVersion,
        )
    }

    private fun DependencyHandler.configureCommonBoms() {
        if (!bomExtension.useCommonBoms.get()) return

        applyBomIfEnabled(bomExtension.useGroovyBom, BOMCoordinates.GROOVY_BOM, bomExtension.groovyBomVersion)
        applyBomIfEnabled(bomExtension.useJacksonBom, BOMCoordinates.JACKSON_BOM, bomExtension.jacksonBomVersion)
        applyBomIfEnabled(bomExtension.useSpringBom, BOMCoordinates.SPRING_BOM, bomExtension.springBomVersion)
        applyBomIfEnabled(bomExtension.useJettyBom, BOMCoordinates.JETTY_BOM, bomExtension.jettyBomVersion)
        applyBomIfEnabled(bomExtension.useNettyBom, BOMCoordinates.NETTY_BOM, bomExtension.nettyBomVersion)
        applyBomIfEnabled(bomExtension.useSlf4jBom, BOMCoordinates.SLF4J_BOM, bomExtension.slf4jBomVersion)
    }

    private fun DependencyHandler.configureTestingBom() {
        applyBomIfEnabled(
            bomExtension.useJunitBom,
            BOMCoordinates.JUNIT_BOM,
            bomExtension.junitBomVersion,
            testOnly = true,
        )
        applyBomIfEnabled(
            bomExtension.useMockitoBom,
            BOMCoordinates.MOCKITO_BOM,
            bomExtension.mockitoBomVersion,
            testOnly = true,
        )
        applyBomIfEnabled(
            bomExtension.useTestcontainersBom,
            BOMCoordinates.TESTCONTAINERS_BOM,
            bomExtension.testcontainersBomVersion,
            testOnly = true,
        )
    }

    private fun DependencyHandler.configureCustomBoms() {
        bomExtension.customBoms.get().forEach { (coordinates, version) ->
            val platformDependency = platform("$coordinates:$version")
            add("implementation", platformDependency)
            add("testImplementation", platform(platformDependency))
        }
    }

    private object BOMCoordinates {
        const val JENKINS_BOM = "io.jenkins.tools.bom:bom-2.504.x"
        const val SPRING_BOM = "org.springframework:spring-framework-bom"
        const val JACKSON_BOM = "com.fasterxml.jackson:jackson-bom"
        const val JUNIT_BOM = "org.junit:junit-bom"
        const val JETTY_BOM = "org.eclipse.jetty:jetty-bom"
        const val SLF4J_BOM = "org.slf4j:slf4j-bom"
        const val NETTY_BOM = "io.netty:netty-bom"
        const val MOCKITO_BOM = "org.mockito:mockito-bom"
        const val TESTCONTAINERS_BOM = "org.testcontainers:testcontainers-bom"
        const val GROOVY_BOM = "org.apache.groovy:groovy-bom"
    }
}
