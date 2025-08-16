[![CI](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/actions/workflows/ci.yml)
[![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin?logo=gradle&label=Gradle%20Plugin%20Portal&labelColor=%2330363c&color=%237b53fb)](https://plugins.gradle.org/plugin/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin)


# Jenkins Gradle Convention Plugin

A Gradle convention plugin for developing Jenkins plugins with modern practices.

# Purpose

The Jenkins Gradle Convention Plugin simplifies and standardizes the development of Jenkins plugins using Gradle. It integrates Jenkins best practices, modern Gradle conventions, and idiomatic Kotlin DSL to provide a robust and developer-friendly build system.

This plugin is designed for:

Jenkins plugin developers looking to move beyond traditional Maven-based workflows.
Gradle users seeking a streamlined and compliant Jenkins plugin development experience.

## Features

- **Modern Build System**: Leverages the latest Gradle features, including configuration caching, build caching, and Kotlin DSL.
- **Dependency Management**: First-class support for Jenkins Bill of Materials (BOM).
- **Quality Tools Integration**: SpotBugs, PMD, Checkstyle, OWASP Dependency Check, and more.
- **Code Style Enforcement**: Built-in support for Kotlin and Java code formatting via Spotless.
- **Testing Enhancements**: PIT mutation testing, JaCoCo code coverage, and compatibility tests for Jenkins versions.

This plugin builds upon the [gradle-jpi-plugin](https://github.com/jenkinsci/gradle-jpi-plugin) while adding modern Gradle practices and enhanced features.

## How to Apply

To use the Jenkins Gradle Convention Plugin in your project, follow these steps:

### Step 1: Add the Plugin to Your `build.gradle.kts`

Add the plugin to your Gradle project using the `plugins {}` block:

```kotlin
plugins {
    id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin") version "<latest-version>"
}
```

Replace `<latest-version>` with the latest version of the plugin available on the Gradle Plugin Portal.

### Step 2: Version Catalog Requirements

In the root `settings.gradle.kts` of the project. Specify the version catalog:

```kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("libs") {
            from("io.github.aaravmahajanofficial:version-catalog:<latest-version>")
        }
    }
}
```

Replace `<latest-version>` with the latest version of the plugin available on the GitHub Releases.

### Step 3: Configure the Plugin

The plugin provides a highly configurable DSL for Jenkins plugin development. Example configuration:

```kotlin
jenkinsConvention {
    // only modify the required fields
    homepage = uri("https://example.com/your-plugin")

    // By default: (id, website, email) would be derived as per configured in git
    developer {
        id = "exampleDev"
        name = "Example Developer"
        email = "example@example.com"
        website = uri("https://example.com")
        organization = "Example Inc."
    }
}
```

### Step 4: Run the Build

Execute the following commands to build and test your Jenkins plugin:

```sh
./gradlew clean build --no-configuration-cache
```

**Notes:**
- The `--no-configuration-cache` flag ensures a clean build and avoids issues when developing plugins.

## Configuration Options

The plugin provides several extensions to customize your build:
- **QualityExtension**: Configure code quality tools like SpotBugs, Detekt, PMD, etc.
- **BomExtension**: Manage dependencies using Jenkins BOMs.
- **PluginExtension**: Set plugin metadata, developers, licenses, and more.

### Quality Tools Example

```kotlin
jenkinsConvention {
    quality {
        spotless {
            enabled = false
        }
    }
}
```

### BOM Management Example

```kotlin
jenkinsConvention {
    bom {
        netty {
            enabled = false
        }
        customBoms {
            create("aws-bom") {
                coordinates = "com.amazonaws:aws-java-sdk-bom"
                testOnly = false
            }
        } 
    }
}
```

## License

This project is licensed under the [Apache License 2.0](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/LICENSE) license.
