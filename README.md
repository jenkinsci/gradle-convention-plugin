# Jenkins Gradle Convention Plugin

A Gradle convention plugin for developing Jenkins plugins with modern practices.

## Features

- **Modern build system** - Uses the latest Gradle features including convention plugins and Kotlin DSL
- **Dependency management** - First-class support for Jenkins Bill of Materials
- **Best Practices** - Enforces Jenkins plugin development best practices

This plugin builds upon the [gradle-jpi-plugin](https://github.com/jenkinsci/gradle-jpi-plugin) while adding modern Gradle practices and enhanced features.

## Project Structure

This plugin consists of the following modules:
- `api` - Core APIs and interfaces
- `core` - Core plugin functionality
- `quality` - Quality checks and reporting
- `bom` - Bill of Materials integration
- `publishing` - Publishing support
- `all` - All-in-one plugin

## Style Guidelines

### Kotlin Style Guide
- Follow the [Kotlin Coding Conventions](https://kotlinlang.org/docs/coding-conventions.html)
- Use the provided `.editorconfig` for consistent formatting
- Use meaningful and descriptive names for classes, methods, variables
- Keep methods small and focused on a single responsibility

## License

This project is licensed under the [MIT](https://github.com/aaravmahajanofficial/jenkins-gradle-convention-plugin/blob/main/LICENSE) license.
