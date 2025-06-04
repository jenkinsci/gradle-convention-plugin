plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}
rootProject.name = "jenkins-gradle-convention-plugin"
include("core")
include("quality")
include("publishing")
include("integration-tests")