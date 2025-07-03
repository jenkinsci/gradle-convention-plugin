package internal

import extensions.BomExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

public class BomManager(
    private val project: Project,
    private val bomExtension: BomExtension,
) {
    private val libs: VersionCatalog = project.extensions.getByType<VersionCatalogsExtension>().named("libs")

    public companion object {
        private const val JENKINS_BOM = "io.jenkins.tools.bom:bom-2.479.x"
        private const val JENKINS_PLUGIN_BOM = "io.jenkins.plugins:plugin-bom"
        private const val SPRING_BOM = "org.springframework:spring-framework-bom"
        private const val JACKSON_BOM = "com.fasterxml.jackson:jackson-bom"
        private const val JUNIT_BOM = "org.junit:junit-bom"
        private const val JETTY_BOM = "org.eclipse.jetty:jetty-bom"
        private const val SLF4J_BOM = "org.slf4j:slf4j-bom"
        private const val NETTY_BOM = "io.netty:netty-bom"
        private const val MOCKITO_BOM = "org.mockito:mockito-bom"
        private const val TESTCONTAINERS_BOM = "org.testcontainers:testcontainers-bom"
        private const val GROOVY_BOM = "org.apache.groovy:groovy-bom"
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
        if (bomExtension.useCoreBom.get()) {
            val bomVersion =
                bomExtension.bomVersion.orNull ?: libs
                    .findVersion("jenkins-bom")
                    .orElseThrow {
                        IllegalStateException("Jenkins BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$JENKINS_BOM:$bomVersion"))
            add("testImplementation", platform("$JENKINS_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configurePluginBom() {
        if (bomExtension.usePluginBom.get()) {
            val bomVersion =
                bomExtension.bomVersion.orNull ?: libs
                    .findVersion("jenkins-plugin-bom")
                    .orElseThrow {
                        IllegalStateException("Jenkins Plugins BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$JENKINS_PLUGIN_BOM:$bomVersion"))
            add("testImplementation", platform("$JENKINS_PLUGIN_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureCommonBoms() {
        if (!bomExtension.useCommonBoms.get()) {
            return
        }

        if (bomExtension.useGroovyBom.get()) {
            val bomVersion =
                bomExtension.groovyBomVersion.orNull ?: libs
                    .findVersion("groovy-bom")
                    .orElseThrow {
                        IllegalStateException("Groovy BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$GROOVY_BOM:$bomVersion"))
            add("testImplementation", platform("$GROOVY_BOM:$bomVersion"))
        }

        if (bomExtension.useJacksonBom.get()) {
            val bomVersion =
                bomExtension.jacksonBomVersion.orNull ?: libs
                    .findVersion("jackson-bom")
                    .orElseThrow {
                        IllegalStateException("Jenkins BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$JACKSON_BOM:$bomVersion"))
            add("testImplementation", platform("$JACKSON_BOM:$bomVersion"))
        }

        if (bomExtension.useSpringBom.get()) {
            val bomVersion =
                bomExtension.springBomVersion.orNull ?: libs
                    .findVersion("spring-bom")
                    .orElseThrow {
                        IllegalStateException("Spring BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$SPRING_BOM:$bomVersion"))
            add("testImplementation", platform("$SPRING_BOM:$bomVersion"))
        }

        if (bomExtension.useJettyBom.get()) {
            val bomVersion =
                bomExtension.jettyBomVersion.orNull ?: libs
                    .findVersion("jetty-bom")
                    .orElseThrow {
                        IllegalStateException("Jetty BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$JETTY_BOM:$bomVersion"))
            add("testImplementation", platform("$JETTY_BOM:$bomVersion"))
        }

        if (bomExtension.useNettyBom.get()) {
            val bomVersion =
                bomExtension.nettyBomVersion.orNull ?: libs
                    .findVersion("netty-bom")
                    .orElseThrow {
                        IllegalStateException("Netty BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$NETTY_BOM:$bomVersion"))
            add("testImplementation", platform("$NETTY_BOM:$bomVersion"))
        }

        if (bomExtension.useSlf4jBom.get()) {
            val bomVersion =
                bomExtension.slf4jBomVersion.orNull ?: libs
                    .findVersion("slf4j-bom")
                    .orElseThrow {
                        IllegalStateException("SLF4J BOM version not found in version catalog")
                    }.requiredVersion

            add("implementation", platform("$SLF4J_BOM:$bomVersion"))
            add("testImplementation", platform("$SLF4J_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureTestingBom() {
        if (bomExtension.useJunitBom.get()) {
            val bomVersion =
                bomExtension.junitBomVersion.orNull ?: libs
                    .findVersion("junit-bom")
                    .orElseThrow {
                        IllegalStateException("JUnit BOM version not found in version catalog")
                    }.requiredVersion

            add("testImplementation", platform("$JUNIT_BOM:$bomVersion"))
        }

        if (bomExtension.useMockitoBom.get()) {
            val bomVersion =
                bomExtension.mockitoBomVersion.orNull ?: libs
                    .findVersion("mockito-bom")
                    .orElseThrow {
                        IllegalStateException("Mockito BOM version not found in version catalog")
                    }.requiredVersion

            add("testImplementation", platform("$MOCKITO_BOM:$bomVersion"))
        }

        if (bomExtension.useTestcontainersBom.get()) {
            val bomVersion =
                bomExtension.testcontainersBom.orNull ?: libs
                    .findVersion("testcontainers-bom")
                    .orElseThrow {
                        IllegalStateException("Testcontainers BOM version not found in version catalog")
                    }.requiredVersion

            add("testImplementation", platform("$TESTCONTAINERS_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureCustomBoms() {
        bomExtension.customBoms.get().forEach { (bomName, bomVersion) ->
            add("implementation", platform("$bomName:$bomVersion"))
            add("testImplementation", platform("$bomName:$bomVersion"))
        }
    }
}
