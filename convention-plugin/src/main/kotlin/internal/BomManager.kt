package internal

import extensions.BomExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.dsl.DependencyHandler
import org.gradle.kotlin.dsl.dependencies

public class BomManager(
    private val project: Project,
    private val bomExtension: BomExtension,
) {
    public companion object {
        private const val JENKINS_BOM = "io.jenkins.tools.bom:bom-2.479.x"
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
            configureCommonBoms()
            configureTestingBom()
            configureCustomBoms()
        }
    }

    private fun DependencyHandler.configureCoreBom() {
        if (bomExtension.useCoreBom.get()) {
            val bomVersion = bomExtension.bomVersion.get()
            add("implementation", platform("$JENKINS_BOM:$bomVersion"))
            add("testImplementation", platform("$JENKINS_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureCommonBoms() {
        if (!bomExtension.useCommonBoms.get()) {
            return
        }

        if (bomExtension.useGroovyBom.get()) {
            val bomVersion =
                bomExtension.groovyBomVersion.get()

            add("implementation", platform("$GROOVY_BOM:$bomVersion"))
            add("testImplementation", platform("$GROOVY_BOM:$bomVersion"))
        }

        if (bomExtension.useJacksonBom.get()) {
            val bomVersion =
                bomExtension.jacksonBomVersion.get()

            add("implementation", platform("$JACKSON_BOM:$bomVersion"))
            add("testImplementation", platform("$JACKSON_BOM:$bomVersion"))
        }

        if (bomExtension.useSpringBom.get()) {
            val bomVersion =
                bomExtension.springBomVersion.get()

            add("implementation", platform("$SPRING_BOM:$bomVersion"))
            add("testImplementation", platform("$SPRING_BOM:$bomVersion"))
        }

        if (bomExtension.useJettyBom.get()) {
            val bomVersion =
                bomExtension.jettyBomVersion.get()

            add("implementation", platform("$JETTY_BOM:$bomVersion"))
            add("testImplementation", platform("$JETTY_BOM:$bomVersion"))
        }

        if (bomExtension.useNettyBom.get()) {
            val bomVersion =
                bomExtension.nettyBomVersion.get()

            add("implementation", platform("$NETTY_BOM:$bomVersion"))
            add("testImplementation", platform("$NETTY_BOM:$bomVersion"))
        }

        if (bomExtension.useSlf4jBom.get()) {
            val bomVersion =
                bomExtension.slf4jBomVersion.get()

            add("implementation", platform("$SLF4J_BOM:$bomVersion"))
            add("testImplementation", platform("$SLF4J_BOM:$bomVersion"))
        }
    }

    private fun DependencyHandler.configureTestingBom() {
        if (bomExtension.useJunitBom.get()) {
            val bomVersion =
                bomExtension.junitBomVersion.get()

            add("testImplementation", platform("$JUNIT_BOM:$bomVersion"))
        }

        if (bomExtension.useMockitoBom.get()) {
            val bomVersion =
                bomExtension.mockitoBomVersion.get()

            add("testImplementation", platform("$MOCKITO_BOM:$bomVersion"))
        }

        if (bomExtension.useTestcontainersBom.get()) {
            val bomVersion =
                bomExtension.testcontainersBom.get()

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
