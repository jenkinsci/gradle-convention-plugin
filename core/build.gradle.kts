//plugins {
//    id("jenkins-convention-common")
//}
//
//dependencies {
//    implementation(libs.gradle.jpi)
//    compileOnly(libs.jenkins.core)
//}
//
//gradlePlugin {
//    plugins {
//        create("jenkinsCoreConvention") {
//            id = "io.jenkins.gradle.jenkins-core-convention"
//            implementationClass = "io.jenkins.gradle.core.JenkinsCoreConventionPlugin"
//            displayName = "Jenkins Gradle Core Convention Plugin"
//            description =
//                "Core Gradle conventions for Jenkins plugin development, including build configuration and JPI integration."
//        }
//    }
//}