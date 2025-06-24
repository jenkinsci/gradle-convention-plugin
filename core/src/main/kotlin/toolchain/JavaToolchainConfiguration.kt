package toolchain

import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.jvm.toolchain.JvmVendorSpec

public object JavaToolchainConfiguration {

    private val DEFAULT_JAVA_VERSION = JavaLanguageVersion.of(17)
    private val DEFAULT_SOURCE_COMPATIBILITY = JavaVersion.VERSION_17
    private val DEFAULT_TARGET_COMPATIBILITY = JavaVersion.VERSION_17

    public fun configureJavaToolchain(project: Project) {

        val configuredVersion = resolveRequiredJavaVersion(project)

        val javaExtension = project.extensions.getByType(JavaPluginExtension::class.java)

        // Java Toolchain Config
        javaExtension.toolchain { toolchain ->
            toolchain.languageVersion.set(configuredVersion)
            toolchain.vendor.set(JvmVendorSpec.ADOPTIUM)
        }

        javaExtension.sourceCompatibility = DEFAULT_SOURCE_COMPATIBILITY
        javaExtension.targetCompatibility = DEFAULT_TARGET_COMPATIBILITY

        javaExtension.modularity.inferModulePath.set(true)
    }

    public fun validateJavaVersion(project: Project) {
        project.afterEvaluate { evaluatedProject ->
            val requiredVersion = resolveRequiredJavaVersion(project)
            val currentJvmVersion = JavaVersion.current()

            if (currentJvmVersion < DEFAULT_SOURCE_COMPATIBILITY) {
                evaluatedProject.logger.warn("Running Gradle with Java $currentJvmVersion, but Java $DEFAULT_SOURCE_COMPATIBILITY is required for Jenkins plugins.")
            }

            evaluatedProject.extensions.findByType(JavaPluginExtension::class.java)?.toolchain?.languageVersion?.orNull?.let { toolchainVersion ->
                if (toolchainVersion < requiredVersion) {
                    val message =
                        "Jenkins plugins require Java ${requiredVersion.asInt()} or later. Configured toolchain: Java ${toolchainVersion.asInt()}"
                    evaluatedProject.logger.warn("WARNING: $message")

                    if (project.findProperty("jenkins.java.strict")?.toString()?.toBoolean() == true) {
                        throw IllegalStateException(message)
                    }
                }
            }

        }
    }

    private fun resolveRequiredJavaVersion(project: Project): JavaLanguageVersion {
        return project.findProperty("jenkins.java.version")?.toString()?.toIntOrNull()
            ?.let { JavaLanguageVersion.of(it) } ?: DEFAULT_JAVA_VERSION
    }
}
