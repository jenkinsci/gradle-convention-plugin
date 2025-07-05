package conventions

import com.diffplug.gradle.spotless.SpotlessExtension
import io.gitlab.arturbosch.detekt.extensions.DetektExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.gradle.kotlin.dsl.withType

public class QualityConventionsPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        with(project) {
            val libs = extensions.getByType<VersionCatalogsExtension>().named("libs")

            pluginManager.apply("com.diffplug.spotless")
            pluginManager.apply("io.gitlab.arturbosch.detekt")

            configureSpotless(libs)
            configureDetekt(libs)

            tasks.named("check") {
                dependsOn("spotlessCheck")
                dependsOn("detekt")
            }
        }
    }
}

private fun Project.configureSpotless(libs: VersionCatalog) {
    configure<SpotlessExtension> {
        val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
        val googleJavaFormat = libs.findVersion("googleJavaFormat").get().requiredVersion

        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**", "bin/**")
            ktlint(ktlintVersion)
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts")
            ktlint(ktlintVersion)
            trimTrailingWhitespace()
            endWithNewline()
        }
        java {
            target("**/*.java")
            googleJavaFormat(googleJavaFormat)
            trimTrailingWhitespace()
            endWithNewline()
            targetExclude("**/generated/**", "**/build/**")
        }
        format("misc") {
            target(
                "*.md",
                ".gitignore",
                "*.properties",
                "*.yml",
                "*.yaml",
                "*.json",
                ".editorconfig",
            )
            targetExclude("**/build/**", "**/.gradle/**", "**/.idea/**")
            trimTrailingWhitespace()
            endWithNewline()
        }

        tasks.named("check").configure { dependsOn("spotlessCheck") }
    }
}

private fun Project.configureDetekt(libs: VersionCatalog) {
    configure<DetektExtension> {
        toolVersion = libs.findVersion("detekt").get().requiredVersion
        parallel = true

        val baseline = rootProject.file("detekt-baseline.xml")
        if (baseline.exists()) {
            this.baseline = baseline
        }
    }

    tasks.withType<io.gitlab.arturbosch.detekt.Detekt>().configureEach {
        jvmTarget = "17"
        autoCorrect = false

        reports {
            html.required.set(true)
            xml.required.set(true)
            sarif.required.set(true)
            txt.required.set(true)
        }
    }
}
