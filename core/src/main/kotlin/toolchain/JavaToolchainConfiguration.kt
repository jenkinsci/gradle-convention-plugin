package toolchain

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType

public object JavaToolchainConfiguration {

    private val REQUIRED_JAVA_VERSION = JavaLanguageVersion.of(17)
    private val SOURCE_COMPATIBILITY = JavaVersion.VERSION_17
    private val TARGET_COMPATIBILITY = JavaVersion.VERSION_17

    public fun configureJavaToolchain(project: Project) {

        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)

        javaExtension.toolchain.languageVersion.set(REQUIRED_JAVA_VERSION)

        javaExtension.sourceCompatibility = SOURCE_COMPATIBILITY
        javaExtension.targetCompatibility = TARGET_COMPATIBILITY

        javaExtension.modularity.inferModulePath.set(true)
    }

    public fun validateJavaVersion(project: Project) {
        val javaExtension = project.extensions.findByType(JavaPluginExtension::class.java) ?: return

        val configuredVersion = javaExtension.toolchain.languageVersion.orNull

        if (configuredVersion != null && configuredVersion < REQUIRED_JAVA_VERSION) {
            throw IllegalStateException("Jenkins plugins require Java ${REQUIRED_JAVA_VERSION.asInt()} or newer but got Java ${configuredVersion.asInt()}.")
        }
    }
}
