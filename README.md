# Jenkins Gradle Convention Plugin

[![CI](https://img.shields.io/github/actions/workflow/status/jenkinsci/gradle-convention-plugin/ci.yml?logo=github&style=for-the-badge&label=CI&labelColor=2a3137)](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/actions/workflows/ci.yml)
[![Plugin Portal](https://img.shields.io/gradle-plugin-portal/v/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin?logo=gradle&label=Plugin%20Portal&style=for-the-badge&labelColor=277A7A&color=2D8B8B)](https://plugins.gradle.org/plugin/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin)
[![License](https://img.shields.io/badge/License-Apache_2.0-1155ba.svg?style=for-the-badge&labelColor=0d479f&logo=apache)](https://opensource.org/licenses/Apache-2.0)
[![Conventional Commits](https://img.shields.io/badge/Conventional%20Commits-1.0.0-fe5196?style=for-the-badge&logo=conventionalcommits&logoColor=white&labelColor=d63b78)](https://conventionalcommits.org)
[![Slack](https://img.shields.io/badge/Slack-%23jenkins--plugin--toolchain-7c3085?style=for-the-badge&logo=slack&logoColor=white&&labelColor=6b2a73)](https://gradle-community.slack.com/archives/C08S0GKMB5G)

<p align="center">
  <img src="docs/img/logo.png" alt="Banner Logo" width="600">
</p>

**Jenkins Gradle Convention Plugin** is a Kotlin-first Gradle convention plugin that eliminates boilerplate and standardizes Jenkins plugin development using Gradle. It provides automated quality checks, CI-friendly defaults, and a unified foundation for building, testing, and publishing Jenkins plugins.

Built on the proven [gradle-jpi-plugin](https://github.com/jenkinsci/gradle-jpi-plugin) with enhanced conventions and integrated tooling, so developers can focus on plugin logic rather than build configuration.

## Getting Started

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

## Features

### Language & Build Conventions

**Modern Language Standards Enforcement**

- Java 21 via toolchains
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

## Project Structure

- **`convention-plugin/src/main/kotlin`**: Core plugin implementation
- **`convention-plugin/src/integrationTest`**: Comprehensive integration tests
- **`build-logic`**: Reusable convention and quality plugins
- **`version-catalogs`**: Centralized dependency versions (libs.versions.toml)

## Contributing

Want to help improve this plugin?

- Fork and open an issue or submit a pull request for any bugs/improvements.
- Review
  [CONTRIBUTING.md](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/CONTRIBUTING.md)
  for guidelines.

## Additional Resources

- [ Gradle Community Project Page ](https://community.gradle.org/events/gsoc/2025/jenkins-plugins-toolchain/) - Overview
  of the project's goals and progress
- [Jenkins.io Blog](https://www.jenkins.io/blog/2025/08/31/aarav-mahajan-gsoc-gradle-convention-plugin-for-jenkins-plugin-development/)
- [ Gradle Plugin Portal ](https://plugins.gradle.org/plugin/io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin/) -
  Official plugin page with installation instructions.
- **Community/Support**: Join the `#jenkins-plugin-toolchain` channel on
  the [Gradle Community Slack](https://community.gradle.org/contributing/community-slack/).

## Acknowledgements

This project started as part of my [Google Summer of Code 2025](https://summerofcode.withgoogle.com/programs/2025/projects/3ujOIGDx) work, under the guidance
of mentors [Oleg Nenashev](https://github.com/oleg-nenashev), [Steve Hill](https://github.com/sghill), and [Rahul Somasunderam](https://github.com/rahulsom), in collaboration with Kotlin Foundation, Gradle and Netflix.

## License

© 2025 Aarav Mahajan. All rights reserved.

This project is licensed under the Apache License 2.0. See the [LICENSE](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/LICENSE) file for details.
