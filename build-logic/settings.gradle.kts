dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal {
            content {
                includeModule("org.gradle.toolchains", "foojay-resolver")
            }
        }
        maven("https://repo.jenkins-ci.org/public/")
    }
    versionCatalogs{
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
include("convention")