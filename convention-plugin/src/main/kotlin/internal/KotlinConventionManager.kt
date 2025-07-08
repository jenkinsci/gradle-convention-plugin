package internal

import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.plugin.KotlinPluginWrapper
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

private const val JAVA_VERSION = 17

public class KotlinConventionManager(
    private val project: Project,
    private val libs: VersionCatalog,
) {
    public fun configure() {
        project.plugins.withType<KotlinPluginWrapper> {
            project.configure<KotlinJvmProjectExtension> {
                jvmToolchain(JAVA_VERSION)
                explicitApi()
            }

            project.tasks.withType<KotlinCompile>().configureEach {
                it.compilerOptions {
                    languageVersion.set(KotlinVersion.KOTLIN_2_2)
                    apiVersion.set(KotlinVersion.KOTLIN_2_2)
                    jvmTarget.set(JvmTarget.JVM_17)
                    allWarningsAsErrors.set(true)
                    freeCompilerArgs.addAll(
                        "-Xjsr305=strict",
                        "-opt-in=kotlin.RequiresOptIn",
                    )
                }
            }

            project.dependencies {
                "implementation"(platform(libs.findLibrary("kotlin-bom").get()))
                "implementation"(libs.findLibrary("kotlin-stdlib").get())
                "implementation"(libs.findLibrary("kotlin-reflect").get())

                "compileOnly"(libs.findLibrary("jetbrains-annotations").get())
            }
        }
    }
}
