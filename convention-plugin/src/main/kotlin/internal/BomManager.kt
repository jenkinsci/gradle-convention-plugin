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
import internal.BomManager.BOMCoordinates.JENKINS_BOM
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.dependencies

public class BomManager(
    private val project: Project,
    private val pluginExtension: PluginExtension,
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
            val platformDependency = enforcedPlatform("$coordinates:${version.get()}")
            if (!testOnly) {
                add("implementation", platformDependency)
            }
            add("testImplementation", platformDependency)
        }
    }

    private fun DependencyHandler.configureCoreBom() {
        applyBomIfEnabled(
            pluginExtension.bomExtension.useCoreBom,
            JENKINS_BOM,
            pluginExtension.bomExtension.bomVersion,
        )
    }

    private fun DependencyHandler.configureCommonBoms() {
        if (!pluginExtension.bomExtension.useCommonBoms.get()) return

        applyBomIfEnabled(
            pluginExtension.bomExtension.useGroovyBom,
            BOMCoordinates.GROOVY_BOM,
            pluginExtension.bomExtension.groovyBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useJacksonBom,
            BOMCoordinates.JACKSON_BOM,
            pluginExtension.bomExtension.jacksonBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useSpringBom,
            BOMCoordinates.SPRING_BOM,
            pluginExtension.bomExtension.springBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useJettyBom,
            BOMCoordinates.JETTY_BOM,
            pluginExtension.bomExtension.jettyBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useNettyBom,
            BOMCoordinates.NETTY_BOM,
            pluginExtension.bomExtension.nettyBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useSlf4jBom,
            BOMCoordinates.SLF4J_BOM,
            pluginExtension.bomExtension.slf4jBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useGuavaBom,
            BOMCoordinates.GUAVA_BOOM,
            pluginExtension.bomExtension.guavaBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useLog4jBom,
            BOMCoordinates.LOG4J_BOM,
            pluginExtension.bomExtension.log4jBomVersion,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useVertxBom,
            BOMCoordinates.VERTX_BOM,
            pluginExtension.bomExtension.vertxBomVersion,
        )
    }

    private fun DependencyHandler.configureTestingBom() {
        applyBomIfEnabled(
            pluginExtension.bomExtension.useJunitBom,
            BOMCoordinates.JUNIT_BOM,
            pluginExtension.bomExtension.junitBomVersion,
            testOnly = true,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useMockitoBom,
            BOMCoordinates.MOCKITO_BOM,
            pluginExtension.bomExtension.mockitoBomVersion,
            testOnly = true,
        )
        applyBomIfEnabled(
            pluginExtension.bomExtension.useTestcontainersBom,
            BOMCoordinates.TESTCONTAINERS_BOM,
            pluginExtension.bomExtension.testcontainersBomVersion,
            testOnly = true,
        )
    }

    private fun DependencyHandler.configureCustomBoms() {
        pluginExtension.bomExtension.customBoms.get().forEach { (coordinates, version) ->
            val platformDependency = enforcedPlatform("$coordinates:$version")
            add("implementation", platformDependency)
            add("testImplementation", platformDependency)
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
        const val GUAVA_BOOM = "com.google.guava:guava-bom"
        const val LOG4J_BOM = "org.apache.logging.log4j:log4j-bom"
        const val VERTX_BOM = "io.vertx:vertx-stack-depchain"
        const val MOCKITO_BOM = "org.mockito:mockito-bom"
        const val TESTCONTAINERS_BOM = "org.testcontainers:testcontainers-bom"
        const val GROOVY_BOM = "org.apache.groovy:groovy-bom"
    }
}
