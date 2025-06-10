plugins {
    base
    alias(libs.plugins.kotlin.jvm) apply false
    java
    `java-gradle-plugin`
}

subprojects {
    group = "io.jenkins.gradle"
    version = providers.gradleProperty("jenkins.convention.version").getOrElse("1.0.0-SNAPSHOT")
}