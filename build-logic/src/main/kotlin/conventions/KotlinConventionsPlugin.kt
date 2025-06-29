package conventions

import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.jvm.toolchain.JavaLanguageVersion
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension

public class KotlinConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            pluginManager.apply("org.jetbrains.kotlin.jvm")
            pluginManager.apply("java-gradle-plugin")

            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            configureKotlin()
            configureCommonDependencies(libs)
        }
    }
}

private fun Project.configureKotlin() {

    extensions.configure<KotlinJvmProjectExtension> {

        jvmToolchain(17)

        explicitApi()

        compilerOptions {
            apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
            languageVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_1)
            jvmTarget.set(JvmTarget.JVM_17)

            allWarningsAsErrors.set(true)
            progressiveMode.set(false)

            freeCompilerArgs.addAll(
                "-Xjsr305=strict",
                "-opt-in=kotlin.RequiresOptIn",
                "-Xjvm-default=all",
            )
        }
    }
}

private fun Project.configureCommonDependencies(libs: VersionCatalog) {
    dependencies {
        "implementation"(platform(libs.findLibrary("kotlin-bom").get()))
        "implementation"(libs.findLibrary("kotlin-stdlib").get())
        "implementation"(libs.findLibrary("kotlin-reflect").get())

        "compileOnly"(libs.findLibrary("jetbrains-annotations").get())

    }
}
