plugins {
    id("conventions.kotlin")
    `maven-publish`
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
            version = "1.0.0"
            implementationClass = "io.jenkins.gradle.JenkinsConventionPlugin"
            displayName = "Jenkins Gradle Convention Plugin"
            description = "Convention plugin for developing Jenkins plugins with Gradle"
            tags.set(listOf("jenkins", "convention", "plugin", "jpi"))

        }

    }

}

publishing {
    publications {
        withType<MavenPublication>().configureEach {
            groupId = "io.jenkins.gradle"
            artifactId = "convention"
            version = "1.0.0"
        }
    }
}
