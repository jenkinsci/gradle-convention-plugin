rootProject.name = "jenkins-gradle-convention-plugin"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        gradlePluginPortal()
        mavenCentral()
        maven {
            name = "Jenkins"
            url = uri("https://repo.jenkins-ci.org/public/")
        }
        maven {
            name = "JenkinsReleases"
            url = uri("https://repo.jenkins-ci.org/releases/")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

include(":all", ":core", ":quality", ":publishing", ":integration-tests", ":examples")

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
enableFeaturePreview("STABLE_CONFIGURATION_CACHE")