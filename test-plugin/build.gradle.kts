plugins {
    id("io.jenkins.gradle.convention") version "1.0.0"
}

repositories {
    mavenCentral()
    gradlePluginPortal()
    google()
}

version = "1.0.0-SNAPSHOT"

jenkinsConvention {
    description = "Checkmarx CxSAST Plugin for Jenkins"
    humanReadableName = "Jenkins Checkmarx Plugin"

    homePage = uri("https://wiki.jenkins-ci.org/display/JENKINS/Checkmarx+CxSAST+Plugin")
    githubUrl = uri("https://github.com/jenkinsci/checkmarx-plugin.git")
    pluginLabels = listOf("security", "static-analysis")

    developer {
        id = "am12"
        name = "Aarav Mahajan"
        portfolioUrl = uri("https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin")
        roles = setOf("dev", "contributor")
        email = "aaravmahajan2003@gmail.com"
        organization = "GSoC, Kotlin Foundation"
    }

    // By default, everything is enabled
    quality {
        checkstyle {
            enabled = false
        }
    }

    // By default, all BOMs are enabled
    bom {
        useNettyBom = false
    }

    // By default, License is set to Apache 2.0
}

dependencies {
    // Project specific BOM-managed dependencies
    // Non-BOM dependencies
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.8.0.202311291450-r")
    implementation("org.codehaus.plexus:plexus-utils:3.5.1")
    implementation("org.iq80.snappy:snappy:0.5")
    compileOnly("com.intellij:annotations:12.0")
    testImplementation("org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5")
    testImplementation("org.jmockit:jmockit:1.16")

    optionalJenkinsPlugins(
        "org.jenkins-ci.main:maven-plugin:3.4@jar",
        "org.jenkins-ci.plugins:credentials:1381.v2c3a_12074da_b_@jar",
    )
}

tasks.named("generateLicenseInfo").configure {
    enabled = false
}
