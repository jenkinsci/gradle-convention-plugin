include("all", "core", "quality", "publishing", "integration-tests", "examples")

dependencyResolutionManagement {
    repositories {
        mavenCentral()
        gradlePluginPortal {
            content {
                includeModule("org.gradle.toolchains", "foojay-resolver")
            }
        }
        maven(url = "https://repo.jenkins-ci.org/public/")
    }
}
includeBuild("build-logic")

rootProject.name = "jenkins-gradle-convention-plugin"