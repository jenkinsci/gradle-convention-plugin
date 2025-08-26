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
package io.github.aaravmahajanofficial.extensions.bom

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.kotlin.dsl.newInstance
import javax.inject.Inject

@Suppress("TooManyFunctions")
public open class BomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val jenkins: JenkinsBomExtension = objects.newInstance(libs)
        public val groovy: GroovyBomExtension = objects.newInstance(libs)
        public val jackson: JacksonBomExtension = objects.newInstance(libs)
        public val spring: SpringBomExtension = objects.newInstance(libs)
        public val netty: NettyBomExtension = objects.newInstance(libs)
        public val slf4j: SLF4JBomExtension = objects.newInstance(libs)
        public val jetty: JettyBomExtension = objects.newInstance(libs)
        public val guava: GuavaBomExtension = objects.newInstance(libs)
        public val log4j: Log4JBomExtension = objects.newInstance(libs)
        public val vertx: VertxBomExtension = objects.newInstance(libs)
        public val junit: JUnitBomExtension = objects.newInstance(libs)
        public val mockito: MockitoBomExtension = objects.newInstance(libs)
        public val testContainers: TestContainersBomExtension = objects.newInstance(libs)
        public val spock: SpockBomExtension = objects.newInstance(libs)
        public val customBoms: NamedDomainObjectContainer<CustomBomExtension> =
            objects.domainObjectContainer(CustomBomExtension::class.java)

        public fun jenkins(action: Action<JenkinsBomExtension>): Unit = action.execute(jenkins)

        public fun groovy(action: Action<GroovyBomExtension>): Unit = action.execute(groovy)

        public fun jackson(action: Action<JacksonBomExtension>): Unit = action.execute(jackson)

        public fun spring(action: Action<SpringBomExtension>): Unit = action.execute(spring)

        public fun netty(action: Action<NettyBomExtension>): Unit = action.execute(netty)

        public fun slf4j(action: Action<SLF4JBomExtension>): Unit = action.execute(slf4j)

        public fun jetty(action: Action<JettyBomExtension>): Unit = action.execute(jetty)

        public fun guava(action: Action<GuavaBomExtension>): Unit = action.execute(guava)

        public fun log4j(action: Action<Log4JBomExtension>): Unit = action.execute(log4j)

        public fun vertx(action: Action<VertxBomExtension>): Unit = action.execute(vertx)

        public fun junit(action: Action<JUnitBomExtension>): Unit = action.execute(junit)

        public fun mockito(action: Action<MockitoBomExtension>): Unit = action.execute(mockito)

        public fun spock(action: Action<SpockBomExtension>): Unit = action.execute(spock)

        public fun testContainers(action: Action<TestContainersBomExtension>): Unit = action.execute(testContainers)

        public fun customBoms(action: Action<NamedDomainObjectContainer<CustomBomExtension>>) {
            action.execute(customBoms)
        }
    }
