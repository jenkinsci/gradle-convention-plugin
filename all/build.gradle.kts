plugins {
    id("jenkins-convention-common")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":quality"))
    implementation(project(":publishing"))
}

gradlePlugin {

    plugins {
        create("jenkinsConventionSuite") {
            id = "io.jenkins.gradle.jenkins-gradle-convention"
            displayName = "Jenkins Gradle Convention Plugin"
            description =
                "A Gradle plugin suite for modern Jenkins plugin development, providing standardized build, quality, and publishing conventions."
            implementationClass = "io.jenkins.gradle.JenkinsGradleConvention"
            tags = setOf("jenkins", "gradle", "plugin", "convention", "jpi", "build", "quality", "publishing")
        }
    }

}