# Contributing to Jenkins Gradle Convention Plugin

Thank you for your interest in contributing! Your help makes this project better for everyone.

---

## Getting Started

1. **Fork the repository** and clone your fork.
2. **Create a new branch** for your change:
   ```sh
   git checkout -b my-feature
   ```
3. **Install prerequisites**:
   - [JDK 17+](https://adoptium.net/)
   - [Gradle 9.x](https://gradle.org/releases/)
   - [Kotlin 2.1+](https://kotlinlang.org/)
4. **Build and test locally**:
   ```sh
   ./gradlew build
   ```

---

## Development Guidelines

- **Code style**: Follow Kotlin and Java idioms. Use `./gradlew spotlessApply` to auto-format.
- **Keep builds reproducible**: Use exact dependency versions.
- **Document public APIs**: KDoc for Kotlin, Javadoc for Java.
- **Add or update tests** for new features or bugfixes.
- **Avoid breaking changes** unless absolutely necessary. If so, clearly document them.

---

## Running Tests

- Run all tests before submitting:
  ```sh
  ./gradlew test
  ```
- For integration tests, see `/src/integrationTest`.

---

## Making a Pull Request

1. **Sync your branch** with `main` before submitting.
2. **Complete the PR template** and describe your changes clearly.
3. **Reference related issues** (e.g., `Closes #123`).
4. **Ensure CI passes**—your PR must be green.
5. **Be responsive** to feedback and requested changes.

---

## Reporting Bugs

- Use the [Bug Report](./.github/ISSUE_TEMPLATE/bug_report.md) template.
- Include:
  - Steps to reproduce
  - Actual and expected behavior
  - Environment info (Gradle, Java, OS, plugin versions)
  - Relevant logs or stack traces

---

## Suggesting Features or Improvements

- Open a new issue or discussion with:
  - Problem statement
  - Proposed solution/feature
  - Example use cases

---

## Resources

- [Gradle Convention Plugins Guide](https://docs.gradle.org/current/samples/sample_convention_plugins.html)
- [Jenkins Plugin Development](https://www.jenkins.io/doc/developer/plugin-development/)
- [Kotlin Documentation](https://kotlinlang.org/docs/home.html)
- [Gradle 9.x User Manual](https://docs.gradle.org/current/userguide/userguide.html)

---

Thank you for helping make this project awesome! 🚀
