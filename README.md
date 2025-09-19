# Jenkins Gradle Convention Plugin

<div align="center">
  <img src="docs/img/logo.png" alt="Banner Logo" width="600px">
</div>

## Overview

[![CI](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/actions/workflows/ci.yml/badge.svg)](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/actions/workflows/ci.yml)
[![Gradle Plugin Portal Version](https://img.shields.io/gradle-plugin-portal/v/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin?logo=gradle&label=Gradle%20Plugin%20Portal&labelColor=%2330363c&color=%237b53fb)](https://plugins.gradle.org/plugin/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin)
[![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg?&labelColor=%2330363c)](https://opensource.org/licenses/Apache-2.0)
[![Slack](https://img.shields.io/badge/Slack-%23jenkins--plugin--toolchain-4A154B?&logo=slack&logoColor=white&&labelColor=%2330363c)](https://gradle-community.slack.com/archives/C08S0GKMB5G)

The **Jenkins Gradle Convention Plugin** is a Kotlin-first, 🐘 [Gradle](https://github.com/gradle/gradle) convention
plugin that acts as the Maven Parent POM equivalent for Jenkins plugin development with Gradle. It provides a unified,
opinionated foundation for building, testing, and publishing Jenkins plugins—standardizing best practices, automating
quality checks, and eliminating boilerplate.

Built on top of the well-established [gradle-jpi-plugin](https://github.com/jenkinsci/gradle-jpi-plugin), this plugin
extends JPI with extra conventions, integrated quality tools, and CI-friendly defaults—so you can focus on your plugin’s
logic, not your build scripts.

> **&check; Stop copy-pasting boilerplate:**
> Get reproducible, high-quality, CI-ready Jenkins plugins out of the box!

---

## Core Features

### Language & Build Conventions

**Modern Language Standards Enforcement**

- Java 21 via toolchains (future-proof)
- Kotlin explicit API mode for better maintainability
- Groovy 4+ conventions for all source sets
- Cross-language compatibility and best practices

### Smart Dependency Management

**Automatic BOM (Bill of Materials) Alignment**

Goodbye, [_dependency
hell_](https://en.wikipedia.org/wiki/Dependency_hell).

- **Zero Version Conflicts**: All major dependencies automatically aligned
- **Supported BOMs**: Jenkins Core, Spring, Jackson, Jetty, Netty, SLF4J, Guava, Log4j, Vert.x, JUnit, Mockito,
  Testcontainers
- **Custom BOM Support**: Project-specific BOMs with runtime/test scoping

### Comprehensive Quality Gates

> [!NOTE]
**Quality tools are applied conditionally based on project sources.**

| Category            | Tools                  | Languages                        | Purpose                                 |
|---------------------|------------------------|----------------------------------|-----------------------------------------|
| **Code Style**      | Spotless               | Java, Kotlin, Groovy, JSON, YAML | Universal formatting and style          |
|                     | Checkstyle             | Java                             | Java-specific style checks              |
|                     | Codenarc               | Groovy                           | Groovy-specific style checks            |
| **Static Analysis** | PMD                    | Java                             | Java bug detection and best practices   |
|                     | SpotBugs               | Java                             | Java bytecode analysis for bugs         |
|                     | Detekt                 | Kotlin                           | Kotlin-specific linting and style       |
| **Security**        | OWASP Dependency-Check | All                              | Vulnerability scanning for dependencies |
| **Coverage**        | Jacoco                 | Java, Groovy                     | JVM test coverage enforcement           |
|                     | Kover                  | Kotlin                           | Kotlin-native coverage analysis         |
| **Testing**         | Pitest                 | Java                             | Mutation testing for robust test suites |
| **Frontend**        | ESLint                 | JavaScript, TypeScript           | Frontend code quality                   |
| **Documentation**   | Dokka                  | Kotlin                           | Kotlin API documentation                |
| **Duplication**     | CPD                    | Java, Groovy                     | Copy-paste detection                    |

**Unified Reporting**

- All reports in standard locations (`build/reports/`)
- Multiple formats: `HTML`, `XML`, `SARIF`
- CI-ready integration with `check` lifecycle

### Modern Testing Ecosystem

**Preconfigured Testing Stack**

- JUnit Jupiter 5.x - Modern test framework
- Kotest - Kotlin-native testing
- MockK & Mockito - Comprehensive mocking
- AssertJ - Fluent assertions
- Spock - Groovy testing framework
- Testcontainers - Integration testing

**Optimized Execution**

- Forked JVMs with sensible memory defaults
- Parallel test execution
- Structured logging and reporting

### Configuration & Extensibility

**Multiple Configuration Approaches**

- Properties-based: Perfect for CI/CD environments
- DSL-based: Rich, type-safe configuration in build scripts
- Multi-module support: Consistent conventions across large projects
- Kotlin DSL optimized: First-class Kotlin build script support

---

## Quick Start

### Step 1: Define the plugin in the projects `libs.versions.toml` file

```toml
[versions]
jenkinsConvention = "<LATEST_VERSION>"

[plugins]
jenkinsConvention = { id = "io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin", version.ref = "jenkinsConvention" }
```

### Step 2: Configure the Version Catalog

In the root `settings.gradle.kts`, add the plugin’s version catalog:

```kts
dependencyResolutionManagement {
    repositories {
        mavenCentral()
    }
    versionCatalogs {
        create("baseLibs") {
            from("io.github.aaravmahajanofficial:version-catalog:<LATEST_VERSION>")
        }
    }
}
```

### Step 3: Basic Build Script

```kotlin
plugins {
    alias(libs.plugins.jenkinsConvention)
}

jenkinsConvention {
    // Only override the defaults that need to be customized for your plugin.

    // Set the Jenkins version (default: as per version catalog).
    jenkinsVersion = "2.525"
}
```

> [!IMPORTANT]
> This is the **minimal working configuration**.
> All later examples (BOMs, quality, etc.) assume you already have this block in place.

---

## Usage Examples

### Customizing BOMs

By default, common BOMs (like Jackson) are applied automatically.
You can disable or add your own:

```kotlin
jenkinsConvention {
    bom {
        // Disable a default BOM
        jackson { enabled = false }

        // Add a custom BOM
        customBoms {
            create("aws-bom") {
                coordinates = "com.amazonaws:aws-java-sdk-bom"
                version = "<AWS_BOM_VERSION>"
                testOnly = false
            }
        }
    }
}
```

### Customizing Quality Tools

The plugin integrates Spotless, OWASP Dependency Check, Detekt, and more.
You can enforce stricter rules or relax defaults:

```kotlin
jenkinsConvention {
    quality {
        spotless {
            enabled = false
        }
        detekt {
            failOnViolation = false // Default: true
        }
        owaspDepCheck {
            enabled = true
            failOnCvss = 7.5f // Default: 7.0f
        }
    }
}
```

---

## Compatibility

| Component   | Supported Versions                                               |
|-------------|------------------------------------------------------------------|
| **Gradle**  | 9.0 or newer                                                     |
| **Jenkins** | Jenkins Core **2.516.2+ (LTS)** – defined via Jenkins Plugin BOM |
| **Java**    | 21 (via toolchains, enforced)                                    |
| **Kotlin**  | 2.2.x+                                                           |
| **Groovy**  | 4.0+ (with BOM alignment)                                        |
| **OS**      | Linux, macOS, Windows                                            |

- **Requires:** Kotlin DSL or Groovy DSL

---

## Project Structure

- **`convention-plugin/src/main/kotlin`**: Core plugin implementation
- **`convention-plugin/src/integrationTest`**: Comprehensive integration tests
- **`build-logic`**: Reusable convention and quality plugins
- **`version-catalogs`**: Centralized dependency versions (libs.versions.toml)

---

## Contributing

Want to help improve this plugin?

- Fork and open an issue or submit a pull request for any bugs/improvements.
- Review
  [CONTRIBUTING.md](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/CONTRIBUTING.md)
  for guidelines.

---

## Additional Resources

- [ Gradle Community Project Page ](https://community.gradle.org/events/gsoc/2025/jenkins-plugins-toolchain/) - Overview
  of the project's goals and progress
- [Jenkins.io Blog](https://www.jenkins.io/blog/2025/08/31/aarav-mahajan-gsoc-gradle-convention-plugin-for-jenkins-plugin-development/)
- [ Gradle Plugin Portal ](https://plugins.gradle.org/plugin/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin/) -
  Official plugin page with installation instructions.
- **Community/Support**: Join the `#jenkins-plugin-toolchain` channel on
  the [Gradle Community Slack](https://community.gradle.org/contributing/community-slack/).

---

## Acknowledgements

This project began
during [Google Summer of Code 2025](https://summerofcode.withgoogle.com/programs/2025/projects/3ujOIGDx) with guidance
from
mentors [Oleg Nenashev](https://github.com/oleg-nenashev), [Steve Hill](https://github.com/sghill) & [Rahul Somasunderam](https://github.com/rahulsom)
and support from the [Kotlin Foundation](https://kotlinfoundation.org/), and continues to be actively maintained.

---

## License

This project is licensed under
the [Apache License 2.0](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/LICENSE).

&copy; 2025 Aarav Mahajan

---

[⬆ Back to Top](#jenkins-gradle-convention-plugin)
