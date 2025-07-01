package internal

import extensions.PublishingExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

public class BomManager(private val project: Project, private val publishingExtension: PublishingExtension) {

    private val libs: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    public companion object {
        private const val JENKINS_BOM = "io.jenkins.tools.bom:bom"
        private const val JENKINS_PLUGIN_BOM = "io.jenkins.plugins:plugin-bom"
        private const val SPRING_BOM = "org.springframework:spring-framework-bom"
        private const val JACKSON_BOM = "com.fasterxml.jackson:jackson-bom"
        private const val JUNIT_BOM = "org.junit:junit-bom"
        private const val JETTY_BOM = "org.eclipse.jetty:jetty-bom"
        private const val SLF4J_BOM = "org.slf4j:slf4j-bom"
        private const val NETTY_BOM = "io.netty:netty-bom"
        private const val MOCKITO_BOM = "org.mockito:mockito-bom"
        private const val TESTCONTAINERS_BOM = "org.testcontainers:testcontainers-bom"
    }

    public fun configure() {
        project.dependencies {
            configureCoreBom()
            configurePluginBom()
            configureCommonBoms()
            configureTestingBom()
            configureCustomBoms()
        }
    }

    private fun DependencyHandler.configureCoreBom() {
        if (publishingExtension.useCoreBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("jenkins-bom").orElseThrow {
                    IllegalStateException("Jenkins BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$JENKINS_BOM-${bomVersion}"))
            add("testImplementation", platform("$JENKINS_BOM-${bomVersion}"))
        }
    }

    private fun DependencyHandler.configurePluginBom() {
        if (publishingExtension.usePluginBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("jenkins-plugin-bom").orElseThrow {
                    IllegalStateException("Jenkins Plugins BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$JENKINS_PLUGIN_BOM:$bomVersion"))
            add("testImplementation", platform("$JENKINS_PLUGIN_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureCommonBoms() {

        if (!publishingExtension.useCommonBoms.get()) {
            return
        }

        if (publishingExtension.useJacksonBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("jackson-bom").orElseThrow {
                    IllegalStateException("Jenkins BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$JACKSON_BOM:$bomVersion"))
            add("testImplementation", platform("$JACKSON_BOM:$bomVersion"))
        }

        if (publishingExtension.useSpringBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("spring-bom").orElseThrow {
                    IllegalStateException("Spring BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$SPRING_BOM:$bomVersion"))
            add("testImplementation", platform("$SPRING_BOM:$bomVersion"))
        }

        if (publishingExtension.useJettyBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("jetty-bom").orElseThrow {
                    IllegalStateException("Jetty BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$JETTY_BOM:$bomVersion"))
            add("testImplementation", platform("$JETTY_BOM:$bomVersion"))
        }

        if (publishingExtension.useNettyBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("netty-bom").orElseThrow {
                    IllegalStateException("Netty BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$NETTY_BOM:$bomVersion"))
            add("testImplementation", platform("$NETTY_BOM:$bomVersion"))
        }

        if (publishingExtension.useSlf4jBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("slf4j-bom").orElseThrow {
                    IllegalStateException("SLF4J BOM version not found in version catalog")
                }.requiredVersion

            add("implementation", platform("$SLF4J_BOM:$bomVersion"))
            add("testImplementation", platform("$SLF4J_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureTestingBom() {

        if (publishingExtension.useJunitBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("junit-bom").orElseThrow {
                    IllegalStateException("JUnit BOM version not found in version catalog")
                }.requiredVersion

            add("testImplementation", platform("$JUNIT_BOM:$bomVersion"))
        }

        if (publishingExtension.useMockitoBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("mockito-bom").orElseThrow {
                    IllegalStateException("Mockito BOM version not found in version catalog")
                }.requiredVersion

            add("testImplementation", platform("$MOCKITO_BOM:$bomVersion"))
        }

        if (publishingExtension.useTestcontainersBom.get()) {
            val bomVersion =
                publishingExtension.bomVersion.orNull ?: libs.findVersion("testcontainers-bom").orElseThrow {
                    IllegalStateException("Testcontainers BOM version not found in version catalog")
                }.requiredVersion

            add("testImplementation", platform("$TESTCONTAINERS_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureCustomBoms() {
        publishingExtension.customBoms.get().forEach { (bomName, bomVersion) ->
            add("implementation", platform("$bomName:$bomVersion"))
            add("testImplementation", platform("$bomName:$bomVersion"))
        }
    }
}
