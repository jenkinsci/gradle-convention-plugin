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

            configureSpotless()
            configureDetekt(libs)

            tasks.named("check") { dependsOn("spotlessCheck", "detekt") }
        }
    }
}

private fun Project.configureSpotless() {
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            targetExclude("**/build/**", "bin/**", "**/generated/**")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
        kotlinGradle {
            target("*.gradle.kts", "**/*.gradle.kts")
            targetExclude("**/build/**", "**/.gradle/**")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }
        java {
            googleJavaFormat()
            target("src/*/java/**/*.java")
            targetExclude("**/generated/**", "**/build/**", "**/.gradle/**")
            trimTrailingWhitespace()
            endWithNewline()
            removeUnusedImports()
        }
        format("misc") {
            target(
                "*.md",
                "*.txt",
                ".gitignore",
                ".gitattributes",
                "*.properties",
                "*.yml",
                "*.yaml",
                "*.json",
                ".editorconfig",
                "*.xml",
                "*.gradle",
                "*.sh",
                "*.dockerfile",
                "Dockerfile*",
            )
            targetExclude(
                "**/build/**",
                "**/.gradle/**",
                "**/.idea/**",
                "**/node_modules/**",
                "**/.git/**",
                "**/generated/**",
            )

            trimTrailingWhitespace()
            endWithNewline()
        }
    }
}

private fun Project.configureDetekt(libs: VersionCatalog) {
    configure<DetektExtension> {
        toolVersion = libs.findVersion("detekt").get().requiredVersion
        parallel = true

        val baseline = rootProject.file("config/quality/detekt-baseline.xml")
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
