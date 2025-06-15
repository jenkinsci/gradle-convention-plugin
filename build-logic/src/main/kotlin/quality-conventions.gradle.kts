import com.diffplug.gradle.spotless.SpotlessExtension

private val libs = the<VersionCatalogsExtension>().named("libs")

configure<SpotlessExtension> {

    isEnforceCheck = true

    format("misc") {
        target("*.gradle", "*.md", ".gitignore", ".editorconfig", ".gitattributes", ".idea/**")
        trimTrailingWhitespace()
        endWithNewline()
    }

    kotlin {
        target("src/**/*.kt")
        ktlint(libs.findVersion("ktlint").get().requiredVersion)
        trimTrailingWhitespace()
        endWithNewline()
        targetExclude("**/generated/**", "**/build/**")
    }

    java {
        target("src/**/*.java")
        googleJavaFormat(libs.findVersion("googleJavaFormat").get().requiredVersion)
        trimTrailingWhitespace()
        endWithNewline()
        targetExclude("**/generated/**", "**/build/**")
    }

    kotlinGradle {
        target("*.gradle.kts")
        ktlint(libs.findVersion("ktlint").get().requiredVersion)
        trimTrailingWhitespace()
        endWithNewline()
    }
}

tasks.named("check") {
    dependsOn("spotlessCheck")
}

tasks.named("build") {
    dependsOn("spotlessApply")
}
