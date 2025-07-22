rootProject.name = "automerger"

pluginManagement {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        mavenLocal()
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from(files("../../version-catalog/libs.versions.toml"))
        }
    }
}

include("lib", "jenkins-plugin")
