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
package extensions

import constants.ConfigurationConstants
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.newInstance
import org.gradle.kotlin.dsl.property
import utils.gradleProperty
import utils.libraryFromCatalog
import javax.inject.Inject

@Suppress("TooManyFunctions")
public open class BomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
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
        public val testContainers: TestcontainersBomExtension = objects.newInstance(libs)
        public val customBoms: NamedDomainObjectContainer<CustomBomsExtension> =
            objects.domainObjectContainer(CustomBomsExtension::class.java)

        public fun jenkins(action: JenkinsBomExtension.() -> Unit): Unit = action(jenkins)

        public fun groovy(action: GroovyBomExtension.() -> Unit): Unit = action(groovy)

        public fun jackson(action: JacksonBomExtension.() -> Unit): Unit = action(jackson)

        public fun spring(action: SpringBomExtension.() -> Unit): Unit = action(spring)

        public fun netty(action: NettyBomExtension.() -> Unit): Unit = action(netty)

        public fun slf4j(action: SLF4JBomExtension.() -> Unit): Unit = action(slf4j)

        public fun jetty(action: JettyBomExtension.() -> Unit): Unit = action(jetty)

        public fun guava(action: GuavaBomExtension.() -> Unit): Unit = action(guava)

        public fun log4j(action: Log4JBomExtension.() -> Unit): Unit = action(log4j)

        public fun vertx(action: VertxBomExtension.() -> Unit): Unit = action(vertx)

        public fun junit(action: JUnitBomExtension.() -> Unit): Unit = action(junit)

        public fun mockito(action: MockitoBomExtension.() -> Unit): Unit = action(mockito)

        public fun testContainers(action: TestcontainersBomExtension.() -> Unit): Unit = action(testContainers)

        public fun customBoms(action: NamedDomainObjectContainer<CustomBomsExtension>.() -> Unit): Unit =
            action(customBoms)
    }

public open class JenkinsBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.JENKINS_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "jenkins-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class GroovyBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.GROOVY_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "groovy-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class JacksonBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.JACKSON_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "jackson-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class SpringBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.SPRING_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "spring-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class NettyBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.NETTY_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "netty-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class SLF4JBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.SLF4J_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(
                libs,
                "slf4j-bom-coordinates",
            )
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class JettyBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.JETTY_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "jetty-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class GuavaBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.GUAVA_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "guava-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class Log4JBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.LOG4J_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "log4j-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class VertxBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.VERTX_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "vertx-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class JUnitBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.JUNIT_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "junit-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class MockitoBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects
                .property<Boolean>()
                .convention(
                    gradleProperty(providers, ConfigurationConstants.MOCKITO_BOM, String::toBoolean).orElse(true),
                )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "mockito-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class TestcontainersBomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        providers: ProviderFactory,
        libs: VersionCatalog,
    ) {
        public val enabled: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    providers,
                    ConfigurationConstants.TESTCONTAINERS_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        internal val coordinates: Provider<MinimalExternalModuleDependency> =
            libraryFromCatalog(libs, "testContainers-bom-coordinates")
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }

public open class CustomBomsExtension
    @Inject
    constructor(
        public val name: String,
        objects: ObjectFactory,
        providers: ProviderFactory,
    ) {
        public val coordinates: Property<String> = objects.property<String>()
        public val version: Property<String> = objects.property<String>()
        public val testOnly: Property<Boolean> = objects.property<Boolean>().convention(false)
    }
