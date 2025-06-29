plugins {
    id("conventions.kotlin")
}

description = "Gradle plugin that provides conventions for developing Jenkins plugins"

dependencies {

    implementation(gradleApi())
    implementation(gradleKotlinDsl())

    implementation(libs.jenkins.gradle.jpi2)

    implementation(libs.spotbugs.gradle.plugin)
    implementation(libs.checkstyle.gradle.plugin)
    implementation(libs.jacoco.gradle.plugin)

}

gradlePlugin {

    plugins {

        create("jenkinsConventions") {

            id = "io.jenkins.gradle.convention"
            implementationClass = "io.jenkins.gradle.JenkinsGradleConventionPlugin"
            displayName = "Jenkins Gradle Convention Plugin"
            description = "Convention plugin for developing Jenkins plugins with Gradle"
            tags.set(listOf("jenkins", "convention", "plugin", "jpi"))

        }

    }

}

