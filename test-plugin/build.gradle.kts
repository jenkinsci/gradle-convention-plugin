/*
 * Copyright 2025 Aarav Mahajan
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
plugins {
    id("io.jenkins.gradle.convention") version "1.0.0"
}

version = "1.0.0-SNAPSHOT"
description = "Checkmarx CxSAST Plugin for Jenkins"

jenkinsConvention {

    // Only need to declare, what is unique to every project
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
//    quality {
//    }

    // By default, all BOMs are enabled
//    bom {
//        useNettyBom = false
//    }

    // By default, License is set to Apache 2.0
}

dependencies {
    // Project specific BOM-managed dependencies
    // Non-BOM dependencies
    implementation("org.eclipse.jgit:org.eclipse.jgit:6.10.1.202505221210-r")
    implementation("org.codehaus.plexus:plexus-utils:3.6.0")
    implementation("org.iq80.snappy:snappy:0.5")
    compileOnly("com.intellij:annotations:12.0")
    testImplementation("org.eclipse.sisu:org.eclipse.sisu.plexus:0.0.0.M5")
    testImplementation("org.jmockit:jmockit:1.16")
}
