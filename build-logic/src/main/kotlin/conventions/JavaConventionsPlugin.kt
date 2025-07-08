package conventions

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

private const val JAVA_VERSION = 17

public class JavaConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("java-library")
            project.configureJava()
        }
    }
}

private fun Project.configureJava() {
    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(JAVA_VERSION))
        withSourcesJar()
        withJavadocJar()

        tasks.withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            options.release.set(JAVA_VERSION)
            options.compilerArgs.addAll(
                listOf(
                    "-parameters",
                    "-Xlint:all,-serial",
                ),
            )
        }

        tasks.withType<AbstractArchiveTask> {
            isPreserveFileTimestamps = false
            isReproducibleFileOrder = true
        }
    }
}
