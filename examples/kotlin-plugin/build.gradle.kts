plugins {
    id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") version "0.1.2-alpha"
}

description = "Checkmarx CxSAST Plugin for Jenkins"
version = "2025.3.3-SNAPSHOT"

jenkinsConvention {
    artifactId = "checkmarx"
    humanReadableName = "Jenkins Checkmarx Plugin"
    homePage = uri("https://wiki.jenkins-ci.org/display/JENKINS/Checkmarx+CxSAST+Plugin")
    githubUrl = uri("https://github.com/jenkinsci/checkmarx-plugin.git")
    pluginLabels = listOf("security", "static-analysis")

    developers {
        developer {
            id = "iland"
            name = "Ilan Dayan"
            email = "dev@checkmarx.com"
            roles = setOf("maintainer", "developer")
            organization = "Checkmarx"
            website = uri("https://www.checkmarx.com")
        }
    }
    // License, quality, and BOMs are set to Jenkins best-practice defaults
}

dependencies {
    // BOM-managed dependencies (auto-managed via convention plugin)

    // Jackson (versions managed by BOM)
    implementation("com.fasterxml.jackson.core:jackson-core")
    implementation("com.fasterxml.jackson.core:jackson-annotations")
    implementation("com.fasterxml.jackson.core:jackson-databind")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jdk8")

    // Netty (BOM-managed, specific modules as needed)
    implementation("io.netty:netty-common")
    implementation("io.netty:netty-buffer")
    implementation("io.netty:netty-transport")
    implementation("io.netty:netty-resolver")
    implementation("io.netty:netty-handler")
    implementation("io.netty:netty-transport-native-unix-common")
    implementation("io.netty:netty-codec")
    implementation("io.netty:netty-handler-proxy")
    implementation("io.netty:netty-codec-socks")
    implementation("io.netty:netty-codec-http2")
    implementation("io.netty:netty-resolver-dns")
    implementation("io.netty:netty-codec-dns")

    // Compile-only annotations
    compileOnly("com.intellij:annotations:12.0")

    // Optional Jenkins plugins (provided at runtime, not bundled)
    optionalJenkinsPlugins("org.jenkins-ci.main:maven-plugin")
    optionalJenkinsPlugins("org.jenkins-ci.plugins:credentials")

    // Test dependencies
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.mockito:mockito-junit-jupiter")
    testImplementation("org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5")
    testImplementation("org.jmockit:jmockit:1.16")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

    // Jenkins test harness plugins
    jenkinsTest("org.jenkins-ci.plugins:ant")
    jenkinsTest("org.jenkins-ci.plugins:mailer")
    jenkinsTest("org.jenkins-ci.plugins:matrix-project")
}

// All quality, coverage, and security tools are enabled and configured via the convention plugin by default.
// To override or tweak quality settings, use:
quality {
    jacoco {
        minimumCodeCoverage = 0.85
    }
    detekt {
        autoCorrect = true
    }
}

// plugins {
//    kotlin("jvm") version "2.2.0"
//    `java-gradle-plugin`
//    id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") version "0.1.2-alpha"
// }
//
// description = "Test - Plugin"
//
// jenkinsConvention {
//    artifactId = "hello"
//    homePage = uri("https://hello-from-test.com")
//
//    quality {
//        kover {
//            enabled = false
//        }
//        spotbugs {
//            enabled = true
//        }
//    }
//
//    scmTag = "HEAD"
//
//    developers {
//        developer {
//            name = "dev"
//            id = "dev"
//            email = "dev@gmail.com"
//        }
//    }
//
//    licenses {
//        license {
//            name = "dev"
//        }
//    }
// }
//
// dependencies {
//    implementation("org.jenkins-ci.plugins:git:4.11.5")
// }
//
//
