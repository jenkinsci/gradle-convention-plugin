plugins {
    id("conventions.kotlin")
    id("conventions.quality")
    `maven-publish`
}

description = "Gradle plugin that provides conventions for developing Jenkins plugins"

dependencies {
    compileOnly(gradleApi())
    compileOnly(gradleKotlinDsl())
    implementation(libs.jenkins.gradle.jpi2)

    implementation(libs.spotless.gradle.plugin)
    implementation(libs.detekt.gradle.plugin)
    implementation(libs.ktlint.gradle.plugin)
    implementation(libs.spotbugs.gradle.plugin)
    implementation(libs.owasp.depcheck.gradle.plugin)
    implementation(libs.benmanes.versions.gradle.plugin)
    implementation(libs.pit.gradle.plugin)
    implementation(libs.kover.gradle.plugin)
    implementation(libs.node.gradle.plugin)
    implementation(libs.dokka.gradle.plugin)
}

gradlePlugin {

    plugins {

        create("jenkinsConventions") {

            id = "io.jenkins.gradle.convention"
            version = "1.0.0"
            implementationClass = "JenkinsConventionPlugin"
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
