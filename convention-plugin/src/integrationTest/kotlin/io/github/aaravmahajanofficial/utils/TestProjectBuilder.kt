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
@file:Suppress("TooManyFunctions")

package io.github.aaravmahajanofficial.utils

import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.exists
import kotlin.io.path.readText
import kotlin.io.path.writeText
import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.gradle.tooling.BuildException

class TestProjectBuilder(
    val projectDir: Path,
) {
    init {
        Files.createDirectories(projectDir)
    }

    fun withVersionCatalog(content: String = defaultVersionCatalog): TestProjectBuilder {
        val catalogDir = projectDir.resolve("gradle")
        Files.createDirectories(catalogDir)
        catalogDir.resolve("libs.versions.toml").writeText(content)
        return this
    }

    fun withSettingsGradle(content: String = defaultSettings): TestProjectBuilder {
        projectDir.resolve("settings.gradle.kts").writeText(content)
        return this
    }

    fun withBuildGradle(content: String): TestProjectBuilder {
        projectDir.resolve("build.gradle.kts").writeText(content)
        return this
    }

    fun withGradleProperties(properties: Map<String, String>): TestProjectBuilder {
        val content =
            buildString {
                appendLine(defaultGradleProperties)
                if (properties.isNotEmpty()) {
                    appendLine()
                    properties.forEach { (key, value) ->
                        appendLine("$key=$value")
                    }
                }
            }
        projectDir.resolve("gradle.properties").writeText(content)
        return this
    }

    fun withJavaSource(
        packageName: String = "com.example",
        className: String = "JavaTestClass",
        content: String? = null,
    ): TestProjectBuilder {
        val sourceDir = projectDir.resolve("src/main/java/${packageName.replace('.', '/')}")
        Files.createDirectories(sourceDir)

        val javaContent =
            content
                ?:
                    """
                    package $packageName;

                    /**
                     * A class that returns a message.
                     * This class is not intended to be subclassed.
                     */
                    public class $className {
                        /**
                         * Returns the message.
                         * @return a greeting string
                         */
                        public String getMessage() {
                            return "Hello from $className :)";
                        }
                    }

                    """.trimIndent()

        sourceDir.resolve("$className.java").writeText(javaContent)
        return this
    }

    fun withKotlinSource(
        packageName: String = "com.example",
        className: String = "KotlinTestClass",
        content: String? = null,
    ): TestProjectBuilder {
        val sourceDir = projectDir.resolve("src/main/kotlin/${packageName.replace('.', '/')}")
        Files.createDirectories(sourceDir)

        val kotlinContent =
            content
                ?:
                    """
                    package $packageName

                    public class $className {
                        public fun getMessage(): String = "Hello from $className :)"
                    }

                    """.trimIndent()

        sourceDir.resolve("$className.kt").writeText(kotlinContent)
        return this
    }

    fun withTestSource(
        packageName: String = "com.example",
        className: String = "JavaTestClassTest",
        testClassName: String = "JavaTestClass",
        language: String = "java",
    ): TestProjectBuilder {
        val testDir =
            when (language) {
                "kotlin" -> projectDir.resolve("src/test/kotlin/${packageName.replace('.', '/')}")
                else -> projectDir.resolve("src/test/java/${packageName.replace('.', '/')}")
            }
        Files.createDirectories(testDir)

        val testContent =
            when (language) {
                "kotlin" ->
                    """
                    package $packageName

                    import org.junit.jupiter.api.Test
                    import org.junit.jupiter.api.Assertions.*

                    class $className {
                        @Test
                        fun testGetMessage() {
                            val testClass = $testClassName()
                            assertEquals("Hello from $testClassName :)", testClass.getMessage())
                        }
                    }

                    """.trimIndent()

                else ->
                    """
                    package $packageName;

                    import org.junit.jupiter.api.Test;
                    import static org.junit.jupiter.api.Assertions.*;

                    public class $className {
                        @Test
                        public void testGetMessage() {
                            $testClassName testClass = new $testClassName();
                            assertEquals("Hello from $testClassName :)", testClass.getMessage());
                        }
                    }

                    """.trimIndent()
            }

        val fileExtension = if (language == "kotlin") "kt" else "java"
        testDir.resolve("$className.$fileExtension").writeText(testContent)
        return this
    }

    fun withJellyFile(path: String = "src/main/resources/index.jelly"): TestProjectBuilder {
        val jellyDir = projectDir.resolve(path)
        Files.createDirectories(jellyDir.parent)

        val jellyContent =
            """
            <?jelly escape-by-default='true'?>
            <div>
                <p>Hello Jenkins Plugin!</p>
            </div>
            """.trimIndent()

        jellyDir.writeText(jellyContent)
        return this
    }

    fun withConfigFile(
        toolName: String,
        fileName: String,
        content: String,
    ): TestProjectBuilder {
        val configDir = projectDir.resolve("config/$toolName")
        Files.createDirectories(configDir)
        configDir.resolve(fileName).writeText(content)
        return this
    }

    fun withPackageJson(content: String? = null): TestProjectBuilder {
        val packageJsonContent =
            content
                ?:
                    """
                    {
                        "name": "test-plugin-frontend",
                        "version": "1.0.0",
                        "scripts": {
                            "lint": "eslint src/main/js/**/*.js"
                        },
                        "devDependencies": {
                            "eslint": "^8.0.0"
                        }
                    }
                    """.trimIndent()

        projectDir.resolve("package.json").writeText(packageJsonContent)
        return this
    }

    fun withJavaScriptSource(path: String = "src/main/js/main.js"): TestProjectBuilder {
        val jsDir = projectDir.resolve(path)
        Files.createDirectories(jsDir.parent)

        val jsContent =
            """
            function hello() {
                console.log("Hello from Jenkins Plugin JS!");
            }
            """.trimIndent()

        jsDir.writeText(jsContent)
        return this
    }

    fun withSubProject(
        name: String,
        action: TestProjectBuilder.() -> Unit,
    ): TestProjectBuilder {
        val subProjectDir = projectDir.resolve(name)
        TestProjectBuilder(subProjectDir).action()

        val settingsFile = projectDir.resolve("settings.gradle.kts")
        if (settingsFile.exists()) {
            val currentContent = settingsFile.readText()
            settingsFile.writeText("$currentContent\ninclude(\":$name\")")
        }

        return this
    }

    fun runGradle(vararg tasks: String): BuildResult = runGradle(tasks.toList())

    fun runGradle(
        tasks: List<String>,
        arguments: List<String> = emptyList(),
        expectFailure: Boolean = false,
    ): BuildResult {
        val allArgs =
            buildList {
                addAll(tasks)
                addAll(arguments)
                add("--stacktrace")
            }

        val runner =
            GradleRunner
                .create()
                .withProjectDir(projectDir.toFile())
                .withArguments(allArgs)
                .withPluginClasspath()
                .withDebug(true)

        return try {
            if (expectFailure) runner.buildAndFail() else runner.build()
        } catch (e: BuildException) {
            throw BuildException("Build failed for tasks: $tasks\n${e.message}", e)
        }
    }

    fun runGradleAndFail(vararg tasks: String): BuildResult = runGradle(tasks.toList(), expectFailure = true)

    companion object {
        fun create(): TestProjectBuilder {
            val tempDir = Files.createTempDirectory("gradle-test-")
            return TestProjectBuilder(tempDir)
        }

        private val defaultGradleProperties =
            """
            org.gradle.parallel=true
            org.gradle.configuration-cache=true
            org.gradle.jvmargs=-Xmx2g
            org.gradle.caching=true
            """.trimIndent()

        private val defaultVersionCatalog =
            """
            [versions]
            kotlin = "2.2.0"
            jenkins-core = "2.520"
            jenkins-gradle-jpi2 = "0.55.0"
            jenkins-bom = "5015.vb_52d36583443"
            spotbugs = "6.2.2"
            detekt = "1.23.8"
            spotless = "7.2.1"
            ktlint = "1.7.1"
            owaspCheck = "12.1.3"
            versions = "0.52.0"
            kover = "0.9.1"
            dokka = "2.0.0"
            pit = "1.15.0"
            checkstyle = "10.12.4"
            jacoco = "0.8.13"
            pmd = "7.16.0"
            codenarc = "3.6.0"
            node-gradle = "7.1.0"
            cpd = "3.5"
            palantir-java = "2.73.0"
            kotest = "6.0.1"
            mockk = "1.14.5"
            assertj = "4.0.0-M1"

            [libraries]
            # Kotlin
            kotlin-bom = { module = "org.jetbrains.kotlin:kotlin-bom", version.ref = "kotlin" }
            kotlin-stdlib = { module = "org.jetbrains.kotlin:kotlin-stdlib", version.ref = "kotlin" }
            kotlin-reflect = { module = "org.jetbrains.kotlin:kotlin-reflect", version.ref = "kotlin" }
            kotlin-gradle-plugin = { module = "org.jetbrains.kotlin:kotlin-gradle-plugin", version.ref = "kotlin" }
            jetbrains-annotations = { module = "org.jetbrains:annotations", version = "26.0.2" }

            # Jenkins
            jenkins-core = { module = "org.jenkins-ci.main:jenkins-core", version.ref = "jenkins-core" }
            jenkins-gradle-jpi2 = { module = "org.jenkins-ci.jpi2:org.jenkins-ci.jpi2.gradle.plugin", version = "0.55.0" }

            # BOM
            jenkins-bom-coordinates = { module = "io.jenkins.tools.bom:bom-2.504.x", version.ref = "jenkins-bom" }
            jackson-bom-coordinates = { module = "com.fasterxml.jackson:jackson-bom", version = "2.19.2" }
            spring-bom-coordinates = { module = "org.springframework:spring-framework-bom", version = "6.2.9" }
            netty-bom-coordinates = { module = "io.netty:netty-bom", version = "4.2.3.Final" }
            slf4j-bom-coordinates = { module = "org.slf4j:slf4j-bom", version = "2.0.17" }
            jetty-bom-coordinates = { module = "org.eclipse.jetty:jetty-bom", version = "12.0.23" }
            guava-bom-coordinates = { module = "com.google.guava:guava-bom", version = "33.4.8-jre" }
            log4j-bom-coordinates = { module = "org.apache.logging.log4j:log4j-bom", version = "2.25.1" }
            vertx-bom-coordinates = { module = "io.vertx:vertx-stack-depchain", version = "5.0.1" }
            junit-bom-coordinates = { module = "org.junit:junit-bom", version = "5.13.4" }
            mockito-bom-coordinates = { module = "org.mockito:mockito-bom", version = "5.18.0" }
            testContainers-bom-coordinates = { module = "org.testcontainers:testcontainers-bom", version = "1.21.3" }
            spock-bom-coordinates = { module = "org.spockframework:spock-bom", version = "2.4-M6-groovy-4.0" }
            
            # Testing
            junit-jupiter = { module = "org.junit.jupiter:junit-jupiter" }
            junit-platform-launcher = { module = "org.junit.platform:junit-platform-launcher" }
            kotest-assertions-core = { module = "io.kotest:kotest-assertions-core", version.ref = "kotest" }
            mockk = { module = "io.mockk:mockk", version.ref = "mockk" }
            mockito-core = { module = "org.mockito:mockito-core" }
            mockito-junit-jupiter = { module = "org.mockito:mockito-junit-jupiter" }
            spock-core = { module = "org.spockframework:spock-core" }
            assertj-core = { module = "org.assertj:assertj-core", version.ref = "assertj" }
            """.trimIndent()

        private val defaultSettings =
            """
            pluginManagement {
                repositories {
                    mavenLocal()
                    gradlePluginPortal()
                }
            }

            rootProject.name = "test-project"

            dependencyResolutionManagement {
                repositories {
                    mavenCentral()
                    maven {
                        name = "Jenkins"
                        url = uri("https://repo.jenkins-ci.org/public/")
                    }
                }
                versionCatalogs {
                    create("baseLibs") {
                        from(files("gradle/libs.versions.toml"))
                    }
                }
            }

            """.trimIndent()
    }
}
