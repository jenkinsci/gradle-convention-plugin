plugins {
    `java-gradle-plugin`
    id("base-conventions")
}

description = "Core functionality and base conventions"

dependencies {
    api(project(":api"))

    implementation(libs.jenkins.gradle.jpi2)

    compileOnly(libs.jenkins.core)
    compileOnly(libs.jenkins.stapler)
}

gradlePlugin {
    plugins {
        create("jenkinsCoreConvention") {
            id = "io.jenkins.gradle.jenkins-core-convention"
            implementationClass = "io.jenkins.gradle.core.JenkinsCoreConventionPlugin"
            displayName = "Jenkins Gradle Core Convention Plugin"
            description =
                "Core Gradle conventions for Jenkins plugin development, including build configuration and JPI integration."
            tags = setOf("jenkins", "plugin", "gradle", "core", "conventions")
        }
    }
}
