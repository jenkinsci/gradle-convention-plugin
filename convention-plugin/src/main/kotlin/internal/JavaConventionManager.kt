package internal

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.bundling.AbstractArchiveTask
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.withType

private const val JAVA_VERSION = 17

public class JavaConventionManager(
    private val project: Project,
) {
    public fun configure() {
        project.plugins.withType<JavaPlugin> {
            project.configure<JavaPluginExtension> {
                toolchain.languageVersion.set(JavaLanguageVersion.of(JAVA_VERSION))
                withSourcesJar()
                withJavadocJar()
            }

            project.tasks.withType<JavaCompile>().configureEach {
                it.options.encoding = "UTF-8"
                it.options.release.set(JAVA_VERSION)
                it.options.compilerArgs.addAll(
                    listOf(
                        "-parameters",
                        "-Xlint:all,-serial",
                    ),
                )
            }

            project.tasks.withType<AbstractArchiveTask> {
                isPreserveFileTimestamps = false
                isReproducibleFileOrder = true
            }
        }
    }
}
