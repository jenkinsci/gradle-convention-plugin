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
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject
import kotlin.jvm.optionals.getOrElse

public open class BomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        private val providers: ProviderFactory,
        private val libs: VersionCatalog,
    ) {
        private fun <T : Any> gradleProperty(
            key: String,
            converter: (String) -> T,
        ): Provider<T> = providers.gradleProperty(key).map(converter)

        public fun gradleProperty(key: String): Provider<String> = providers.gradleProperty(key)

        private fun versionFromCatalogOrFail(alias: String): String =
            libs
                .findVersion(alias)
                .getOrElse {
                    error(
                        "Version '$alias' missing from version catalog. Please update 'libs.versions.toml'.",
                    )
                }.requiredVersion

        // Jenkins BOM
        public val useCoreBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_CORE_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val bomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.CORE_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("jenkins-bom"),
                ),
            )

        // Ecosystem BOM
        public val useCommonBoms: Property<Boolean> = objects.property<Boolean>().convention(true)

        public val useGroovyBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_GROOVY_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val groovyBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.GROOVY_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("groovy-bom"),
                ),
            )

        public val useJacksonBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_JACKSON_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val jacksonBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.JACKSON_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("jackson-bom"),
                ),
            )

        public val useSpringBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_SPRING_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val springBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.SPRING_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("spring-bom"),
                ),
            )

        public val useNettyBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_NETTY_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val nettyBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.NETTY_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("netty-bom"),
                ),
            )

        public val useSlf4jBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_SLF4J_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val slf4jBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.SLF4J_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("slf4j-bom"),
                ),
            )

        public val useJettyBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_JETTY_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val jettyBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.JETTY_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("jetty-bom"),
                ),
            )

        // Testing BOM
        public val useJunitBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_JUNIT_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val junitBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.JUNIT_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("junit-bom"),
                ),
            )

        public val useMockitoBom: Property<Boolean> =
            objects
                .property<Boolean>()
                .convention(gradleProperty(ConfigurationConstants.USE_MOCKITO_BOM, String::toBoolean).orElse(true))
        public val mockitoBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.MOCKITO_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("mockito-bom"),
                ),
            )

        public val useTestcontainersBom: Property<Boolean> =
            objects.property<Boolean>().convention(
                gradleProperty(
                    ConfigurationConstants.USE_TESTCONTAINERS_BOM,
                    String::toBoolean,
                ).orElse(true),
            )
        public val testcontainersBomVersion: Property<String> =
            objects.property<String>().convention(
                gradleProperty(ConfigurationConstants.TESTCONTAINERS_BOM_VERSION).orElse(
                    versionFromCatalogOrFail("testcontainers-bom"),
                ),
            )

        public val customBoms: MapProperty<String, String> = objects.mapProperty<String, String>()
    }
