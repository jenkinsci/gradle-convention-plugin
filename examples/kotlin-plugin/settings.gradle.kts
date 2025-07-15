pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenCentral()

        maven {
            name = "Jenkins"
            url = uri("https://repo.jenkins-ci.org/public/")
        }
        maven {
            name = "Jenkins"
            url = uri("https://repo.jenkins-ci.org/releases/")
        }
    }

    versionCatalogs {
        create("libs") {
            from("io.github.aaravmahajanofficial:version-catalog:0.1.2-alpha")
        }
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}

rootProject.name = "test-plugin"
