plugins {
    id("kotlin-conventions")
    `java-gradle-plugin`
}

dependencies {
    implementation(project(":api"))
    implementation(libs.jenkins.gradle.jpi)
}

gradlePlugin {
    plugins {
        create("jenkinsCoreConvention") {
            id = "io.jenkins.gradle.jenkins-core-convention"
            displayName = "Jenkins Gradle Core Convention Plugin"
            implementationClass = "io.jenkins.gradle.core.JenkinsCoreConventionPlugin"
            description =
                "Core Gradle conventions for Jenkins plugin development, including build configuration and JPI integration."
        }
    }
}