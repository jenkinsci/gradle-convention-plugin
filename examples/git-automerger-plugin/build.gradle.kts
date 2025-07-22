plugins {
    kotlin("jvm") version "2.2.0" apply false
    id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") version "0.2.1-alpha"
}

allprojects {
    repositories {
        mavenLocal()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://jitpack.io") }
        maven { url = uri("https://repo.jenkins-ci.org/public/") }
        maven { url = uri("https://repo.jenkins-ci.org/releases/") }
    }
}
