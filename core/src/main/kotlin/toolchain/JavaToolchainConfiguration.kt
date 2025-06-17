package toolchain

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.getByType

public object JavaToolchainConfiguration {

    private val REQUIRED_JAVA_VERSION = JavaLanguageVersion.of(17)
    private val COMPATIBILITY = JavaVersion.VERSION_17

    public fun configureJavaToolchain(project: Project) {

        val javaExt = project.extensions.getByType<JavaPluginExtension>()
        javaExt.toolchain.languageVersion.set(REQUIRED_JAVA_VERSION)

        javaExt.sourceCompatibility = COMPATIBILITY
        javaExt.targetCompatibility = COMPATIBILITY

        javaExt.modularity.inferModulePath.set(true)
    }

    public fun validateJavaVersion(project: Project) {
        val javaExt = project.extensions.findByType(JavaPluginExtension::class.java) ?: return

        val configuredVersion = javaExt.toolchain.languageVersion.orNull
        if (configuredVersion != null && configuredVersion.asInt() < REQUIRED_JAVA_VERSION.asInt()) {
            throw IllegalStateException("Jenkins plugins require Java ${REQUIRED_JAVA_VERSION.asInt()} or newer but got Java ${configuredVersion.asInt()}.")
        }
    }
}
