plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
}

dependencies {
    implementation("org.slf4j:slf4j-api")
    runtimeOnly("org.slf4j:slf4j-simple")

    implementation("org.apache.maven:maven-artifact:3.6.1")
    testImplementation("lt.neworld:kupiter:1.0.0")
    testImplementation("com.google.auto.service:auto-service:1.0-rc5")

    kaptTest("com.google.auto.service:auto-service:1.0-rc5")
}

tasks.test {
    useJUnitPlatform()
    jvmArgs("-Dorg.slf4j.simpleLogger.defaultLogLevel=debug")
}
