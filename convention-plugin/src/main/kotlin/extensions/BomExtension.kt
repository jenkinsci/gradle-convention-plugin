package extensions

import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.MapProperty
import org.gradle.api.provider.Property
import org.gradle.kotlin.dsl.mapProperty
import org.gradle.kotlin.dsl.property
import javax.inject.Inject

public abstract class BomExtension
    @Inject
    constructor(
        objects: ObjectFactory,
        libs: VersionCatalog,
    ) {
        public val useGithubReleases: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val generateChangelog: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val useIncrementalRepo: Property<Boolean> = objects.property<Boolean>().convention(false)

        // Jenkins BOM
        public val useCoreBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val bomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("jenkins-bom")
                    .get()
                    .requiredVersion,
            )

        // Ecosystem BOM
        public val useCommonBoms: Property<Boolean> = objects.property<Boolean>().convention(true)

        public val useGroovyBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val groovyBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("groovy-bom")
                    .get()
                    .requiredVersion,
            )

        public val useJacksonBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val jacksonBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("jackson-bom")
                    .get()
                    .requiredVersion,
            )

        public val useSpringBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val springBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("spring-bom")
                    .get()
                    .requiredVersion,
            )

        public val useNettyBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val nettyBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("netty-bom")
                    .get()
                    .requiredVersion,
            )

        public val useSlf4jBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val slf4jBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("slf4j-bom")
                    .get()
                    .requiredVersion,
            )

        public val useJettyBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val jettyBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("jetty-bom")
                    .get()
                    .requiredVersion,
            )

        // Testing BOM
        public val useJunitBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val junitBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("junit-bom")
                    .get()
                    .requiredVersion,
            )

        public val useMockitoBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val mockitoBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("mockito-bom")
                    .get()
                    .requiredVersion,
            )

        public val useTestcontainersBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val testcontainersBomVersion: Property<String> =
            objects.property<String>().convention(
                libs
                    .findVersion("testcontainers-bom")
                    .get()
                    .requiredVersion,
            )

        public val customBoms: MapProperty<String, String> = objects.mapProperty<String, String>()
    }
