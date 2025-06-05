rootProject.name = "jenkins-gradle-convention-plugin"
include(":core", ":quality", ":publishing", ":integration-tests", ":all", ":examples")

pluginManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal()
        maven("https://repo.jenkins-ci.org/public/")
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenCentral()
        gradlePluginPortal {
            content {
                includeModule("org.gradle.toolchains", "foojay-resolver")
            }
        }
        maven("https://repo.jenkins-ci.org/public/")
    }
}