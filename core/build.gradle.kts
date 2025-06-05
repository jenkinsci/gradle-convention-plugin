plugins {
    kotlin("jvm")
}

group = "io.jenkins.gradle"
version = "1.0-SNAPSHOT"


dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}