enableFeaturePreview("STABLE_CONFIGURATION_CACHE")

rootProject.name = "jenkins-gradle-convention-plugin"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
        mavenLocal()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

@Suppress("UnstableApiUsage")
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.PREFER_PROJECT)

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
        maven {
            name = "JenkinsIncrementals"
            url = uri("https://repo.jenkins-ci.org/incrementals/")
        }
    }
}

include("convention-plugin")
// include("test-plugin")
