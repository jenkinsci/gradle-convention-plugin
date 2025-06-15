import common.javaLanguageVersion

plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion = javaLanguageVersion()
    }

    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaLanguageVersion().map { it.asInt() })
    options.compilerArgs.addAll(
        listOf(
            "-parameters",
            "-Xlint:deprecation",
            "Xlint:unchecked"
        )
    )
}

tasks.withType<AbstractArchiveTask>() {
    isPreserveFileTimestamps = false
    isReproducibleFileOrder = true
}