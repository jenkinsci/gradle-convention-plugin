package extensions

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
    ) {
        public val useGithubReleases: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val generateChangelog: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val useIncrementalRepo: Property<Boolean> = objects.property<Boolean>().convention(false)

        // Jenkins BOM
        public val useCoreBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val bomVersion: Property<String> = objects.property<String>()
        public val usePluginBom: Property<Boolean> = objects.property<Boolean>().convention(true)

        // Ecosystem BOM
        public val useCommonBoms: Property<Boolean> = objects.property<Boolean>().convention(true)

        public val useGroovyBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val groovyBomVersion: Property<String> = objects.property<String>()

        public val useJacksonBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val jacksonBomVersion: Property<String> = objects.property<String>()

        public val useSpringBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val springBomVersion: Property<String> = objects.property<String>()

        public val useNettyBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val nettyBomVersion: Property<String> = objects.property<String>()

        public val useSlf4jBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val slf4jBomVersion: Property<String> = objects.property<String>()

        public val useJettyBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val jettyBomVersion: Property<String> = objects.property<String>()

        // Testing BOM
        public val useJunitBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val junitBomVersion: Property<String> = objects.property<String>()

        public val useMockitoBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val mockitoBomVersion: Property<String> = objects.property<String>()

        public val useTestcontainersBom: Property<Boolean> = objects.property<Boolean>().convention(true)
        public val testcontainersBom: Property<String> = objects.property<String>()

        public val customBoms: MapProperty<String, String> = objects.mapProperty<String, String>()
    }
