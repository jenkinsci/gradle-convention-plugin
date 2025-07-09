# Jenkins Gradle Convention Plugin

A Gradle convention plugin for developing Jenkins plugins with modern practices.

## Features

- **Modern build system** - Uses the latest Gradle features including convention plugins and Kotlin DSL
- **Dependency management** - First-class support for Jenkins Bill of Materials
- **Best Practices** - Enforces Jenkins plugin development best practices

This plugin builds upon the [gradle-jpi-plugin](https://github.com/jenkinsci/gradle-jpi-plugin) while adding modern Gradle practices and enhanced features.

## Project Structure

`convention-plugin` - Core plugin functionality

## How to use

### 1. Publish the Convention Plugin to Maven Local

```sh
./gradlew :convention-plugin:publishToMavenLocal --no-daemon --no-configuration-cache
```

### 2. Build and Test the Consumer Jenkins Plugin

```sh
./gradlew :test-plugin:clean build check --no-configuration-cache --stacktrace
```

**Notes:**
- Always run the `publishToMavenLocal` command after making changes to the convention plugin, so your consumer project picks up the latest version.
- The `--no-configuration-cache` flag ensures a clean build and avoids issues when developing plugins.
- The `--stacktrace` flag provides detailed error output for troubleshooting.

## Style Guidelines

### Kotlin Style Guide
- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use the provided `.editorconfig` for consistent formatting
- Use meaningful and descriptive names for classes, methods, variables
- Keep methods small and focused on a single responsibility

## License

This project is licensed under the [Apache License 2.0](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/LICENSE) license.
