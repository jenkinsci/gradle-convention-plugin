import org.jetbrains.kotlin.gradle.internal.KaptGenerateStubsTask
import org.jetbrains.kotlin.gradle.internal.KaptTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    kotlin("kapt")
    id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
}

description =
    "The main purpose of this plugin is to ensure that all newer versions include all the changes from older versions."

jenkinsConvention {
    artifactId = "git-automerger"
    homePage = uri("https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin")
}

dependencies {
    implementation(project(":lib"))
    implementation("com.vinted:slf4j-streamadapter:1.0.0")
    kapt("net.java.sezpoz:sezpoz:1.13")
}

tasks.withType<KotlinCompile> {
    compilerOptions {
        allWarningsAsErrors = false
    }
}

kapt {
    correctErrorTypes = true
    includeCompileClasspath = false
}

tasks.withType(KaptTask::class.java).all {
    outputs.upToDateWhen { false }
}

tasks.withType(KaptGenerateStubsTask::class.java).all {
    outputs.upToDateWhen { false }
}


// Checkmarx Dependencies

//dependencies {
//
//
//    implementation(project(":lib"))
//    implementation("com.vinted:slf4j-streamadapter:1.0.0")
//    kapt("net.java.sezpoz:sezpoz:1.13")
//
//
//    // Jackson
//    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
//    compileOnly("com.fasterxml.jackson.core:jackson-core")
//    compileOnly("com.fasterxml.jackson.core:jackson-annotations")
//
//    // Logging
//    compileOnly("org.apache.logging.log4j:log4j-slf4j-impl")
//    compileOnly("org.apache.logging.log4j:log4j-api")
//    compileOnly("org.apache.logging.log4j:log4j-core")
//
//    // Netty
//    compileOnly("io.netty:netty-common")
//    compileOnly("io.netty:netty-buffer")
//    compileOnly("io.netty:netty-transport")
//    compileOnly("io.netty:netty-resolver")
//
//    compileOnly("com.google.guava:guava")
//    compileOnly("io.vertx:vertx-core")
//    implementation("io.vertx:vertx-web")
//
//    testCompileOnly("junit:junit")
//    testImplementation("org.junit.jupiter:junit-jupiter-api")
//    testImplementation("org.mockito:mockito-junit-jupiter")
//    testImplementation("org.jenkins-ci.plugins:ant")
//    testImplementation("org.jenkins-ci.plugins:mailer")
//    testImplementation("org.jenkins-ci.plugins:matrix-project")
//    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
//    testRuntimeOnly("org.mockito:mockito-junit-jupiter")
//
//
//    // NON-BOM Dependencies
//    compileOnly("org.yaml:snakeyaml:2.2")
//    compileOnly("org.json:json:20231013")
//    compileOnly("com.google.code.gson:gson:2.8.9")
//    testImplementation(kotlin("test"))
//}
