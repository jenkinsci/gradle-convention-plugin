package common

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.gradle.jvm.toolchain.JavaLanguageVersion

fun Project.javaLanguageVersion(): Provider<JavaLanguageVersion> =
    providers.gradleProperty("java.toolchain.version").map { JavaLanguageVersion.of(it.toInt()) }.orElse(
        JavaLanguageVersion.of(17)
    )