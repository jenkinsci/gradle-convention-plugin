plugins {
    id("kotlin-conventions")
}

subprojects {
    group = "io.jenkins.gradle"
    version = providers.gradleProperty("jenkins.convention.version").getOrElse("1.0.0-SNAPSHOT")
}