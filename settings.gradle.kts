enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "jenkins-gradle-convention-plugin"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
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

include(":all", ":api", ":core", ":quality", ":publishing", ":integration-tests", ":examples")