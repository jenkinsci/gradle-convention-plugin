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
@file:Suppress("FunctionName")

package io.github.aaravmahajanofficial

import io.github.aaravmahajanofficial.utils.TestProjectBuilder
import io.github.aaravmahajanofficial.utils.basicBuildScript
import io.github.aaravmahajanofficial.utils.basicPluginConfiguration
import io.kotest.matchers.paths.shouldExist
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import javax.xml.parsers.DocumentBuilderFactory

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Quality Tools Integration Tests")
class QualityToolsIntegrationTest {
    lateinit var builder: TestProjectBuilder

    @AfterEach
    fun cleanupTestProject() {
        builder.cleanup()
    }

    @Test
    @DisplayName("should execute the checkstyle with defaults")
    fun `execute checkstyle with defaults`() {
        builder =
            TestProjectBuilder
                .create("checkstyle-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource()

        val result = builder.runGradleAndFail("checkstyleMain")
        result.task(":checkstyleMain")?.outcome shouldBe TaskOutcome.FAILED

        val xmlReport = builder.projectDir.resolve("build/reports/checkstyle/main.xml")
        val htmlReport = builder.projectDir.resolve("build/reports/checkstyle/main.html")
        xmlReport.shouldExist()
        htmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("error")

        violations.length shouldBe 1
    }

    @Test
    @DisplayName("should suppress the checkstyle violations")
    fun `checkstyle should skip violations due to suppressions`() {
        builder =
            TestProjectBuilder
                .create("checkstyle-suppress-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource()
                .withConfigFile(
                    toolName = "checkstyle",
                    fileName = "suppressions.xml",
                    content =
                        """
                        <?xml version="1.0" encoding="UTF-8"?>
                        <!DOCTYPE suppressions PUBLIC
                            "-//Checkstyle//DTD SuppressionFilter Configuration 1.0//EN"
                            "https://checkstyle.org/dtds/suppressions_1_0.dtd">
                        <suppressions>
                          <suppress checks="JavadocPackage" files=".*[\\/]src[\\/]main[\\/]java[\\/]com[\\/]example[\\/].*"/>
                        </suppressions>
                        """.trimIndent(),
                )

        val result = builder.runGradle("checkstyleMain")
        result.task(":checkstyleMain")?.outcome shouldBe TaskOutcome.SUCCESS

        val xmlReport = builder.projectDir.resolve("build/reports/checkstyle/main.xml")
        xmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("error")

        violations.length shouldBe 0
    }

    @Test
    @DisplayName("should execute spotbugs with defaults")
    fun `execute spotbugs with defaults`() {
        builder =
            TestProjectBuilder
                .create("spotbugs-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource(
                    content =
                        """
                        package com.example;

                        public class JavaTestClass {

                            /**
                             * This field is never read or used.
                             * SpotBugs should report this as 'URF_UNREAD_FIELD'.
                             */
                            private int unusedField = 123;
                        }

                        """.trimIndent(),
                )

        val result = builder.runGradleAndFail("spotbugsMain")
        result.task(":spotbugsMain")?.outcome shouldBe TaskOutcome.FAILED

        val xmlReport = builder.projectDir.resolve("build/reports/spotbugs/main.xml")
        val htmlReport = builder.projectDir.resolve("build/reports/spotbugs/main.html")
        val sarifReport = builder.projectDir.resolve("build/reports/spotbugs/main.sarif")
        xmlReport.shouldExist()
        htmlReport.shouldExist()
        sarifReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("BugInstance")

        violations.length shouldBe 1
    }

    @Test
    @DisplayName("should execute the spotbugs with exclusion filters")
    fun `execute spotbugs with exclude filters`() {
        builder =
            TestProjectBuilder
                .create("spotbugs-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource(
                    content =
                        """
                        package com.example;

                        public class JavaTestClass {

                            /**
                             * This field is never read or used.
                             * SpotBugs should report this as 'URF_UNREAD_FIELD'.
                             */
                            private int unusedField = 123;
                        }

                        """.trimIndent(),
                ).withConfigFile(
                    toolName = "spotbugs",
                    fileName = "excludesFilter.xml",
                    content =
                        """
                        <FindBugsFilter>
                            <Match>
                                <Class name="com.example.JavaTestClass"/>
                                <Bug pattern="URF_UNREAD_FIELD"/>
                            </Match>
                        </FindBugsFilter>
                        """.trimIndent(),
                )

        val result = builder.runGradle("spotbugsMain")
        result.task(":spotbugsMain")?.outcome shouldBe TaskOutcome.SUCCESS

        val xmlReport = builder.projectDir.resolve("build/reports/spotbugs/main.xml")
        xmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("BugInstance")

        violations.length shouldBe 0
    }

    @Test
    @DisplayName("should execute pmd with defaults")
    fun `execute pmd with defaults`() {
        builder =
            TestProjectBuilder
                .create("pmd-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource(
                    content =
                        """
                        package com.example;

                        @SuppressWarnings("PMD.UnusedLocalVariable")
                        public class JavaTestClass {

                            public void doSomething() {
                                try {
                                    int x = 5 / 0;
                                } catch (Exception e) {
                                    // EmptyCatchBlock
                                }
                            }
                        }

                        """.trimIndent(),
                )

        val result = builder.runGradleAndFail("pmdMain")
        result.task(":pmdMain")?.outcome shouldBe TaskOutcome.FAILED

        val xmlReport = builder.projectDir.resolve("build/reports/pmd/main.xml")
        val htmlReport = builder.projectDir.resolve("build/reports/pmd/main.html")
        xmlReport.shouldExist()
        htmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("violation")

        violations.length shouldBe 1
    }

    @Test
    @DisplayName("should execute cpd with defaults")
    fun `execute cpd with defaults`() {
        builder =
            TestProjectBuilder
                .create("cpd-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource()
                .withJavaSource(
                    className = "JavaTestClass",
                    content =
                        """
                        package com.example;

                        public class JavaTestClass {
                            public String getMessage() {
                                String message = "Hello from JavaTestClass";
                                System.out.println(message);
                                return message;
                            }

                            public void duplicateMethod() {
                                String duplicateCode = "This is duplicate code";
                                System.out.println(duplicateCode);
                                System.out.println("Line 1");
                                System.out.println("Line 2");
                                System.out.println("Line 3");
                                System.out.println("Line 4");
                                System.out.println("Line 5");
                            }
                        }
                        """.trimIndent(),
                ).withJavaSource(
                    className = "JavaTestClass2",
                    content =
                        """
                        package com.example;

                        public class JavaTestClass2 {
                            public String getMessage() {
                                String message = "Hello from JavaTestClass2";
                                System.out.println(message);
                                return message;
                            }

                            public void anotherDuplicateMethod() {
                                String duplicateCode = "This is duplicate code";
                                System.out.println(duplicateCode);
                                System.out.println("Line 1");
                                System.out.println("Line 2");
                                System.out.println("Line 3");
                                System.out.println("Line 4");
                                System.out.println("Line 5");
                            }
                        }
                        """.trimIndent(),
                )

        val result = builder.runGradleAndFail("cpdCheck")
        result.task(":cpdCheck")?.outcome shouldBe TaskOutcome.FAILED

        val xmlReport = builder.projectDir.resolve("build/reports/cpd/cpdCheck.xml")
        xmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("duplication")

        violations.length shouldBe 1
    }

//    @Test
//    @DisplayName("should execute Jacoco with defaults")
//    fun `execute jacoco with defaults`() {
//        builder =
//            TestProjectBuilder
//                .create("jacoco-test")
//                .withVersionCatalog()
//                .withSettingsGradle()
//                .withGradleProperties(
//                    mapOf(
//                        "org.gradle.jvmargs" to
//                            "--add-opens=java.prefs/java.util.prefs=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.lang.invoke=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.lang=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.io=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.util=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.lang.reflect=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.security=ALL-UNNAMED" +
//                            "--add-opens=java.base/java.net=ALL-UNNAMED" +
//                            "--add-opens=java.base/sun.nio.ch=ALL-UNNAMED" +
//                            "--add-exports=java.base/sun.nio.ch=ALL-UNNAMED" +
//                            "--add-exports=jdk.unsupported/sun.misc=ALL-UNNAMED",
//
//                        "org.gradle.daemon" to "false"
//                    ),
//                ).withBuildGradle(
//                    """
//                    plugins {
//                        `jvm-test-suite`
//                        `java-gradle-plugin`
//                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
//                    }
//
//                    testing {
//                        suites {
//                            val test by getting(JvmTestSuite::class) {
//                                useJUnitJupiter()
//                            }
//                        }
//                    }
//
//                    jenkinsConvention {
//                        quality {
//                            jacoco {
//                                minimumCodeCoverage = 0.5
//                            }
//                        }
//                    }
//
//                    """.trimIndent(),
//                ).withJavaSource()
//                .withTestSource()
//
//        val result = builder.runGradle("test", "jacocoTestReport", "jacocoTestCoverageVerification")
//        result.task("test")?.outcome shouldBe TaskOutcome.SUCCESS
//        result.task("jacocoTestReport")?.outcome shouldBe TaskOutcome.SUCCESS
//        result.task("jacocoTestCoverageVerification")?.outcome shouldBe TaskOutcome.SUCCESS
//
//        val xmlReport = builder.projectDir.resolve("build/reports/jacoco/test/jacocoTestReport.xml")
//        val htmlReport = builder.projectDir.resolve("build/reports/jacoco/test/html/index.html")
//        val csvReport = builder.projectDir.resolve("build/reports/jacoco/test/jacocoTestReport.csv")
//
//        println(xmlReport.readText())
//
//        xmlReport.shouldExist()
//        htmlReport.shouldExist()
//        csvReport.shouldExist()
//    }

    @Test
    @DisplayName("should execute Detekt kotlin code analysis")
    fun `execute detekt for kotlin code analysis`() {
        builder =
            TestProjectBuilder
                .create("detekt-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        kotlin("jvm")
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    ${basicPluginConfiguration()}

                    """.trimIndent(),
                ).withKotlinSource(
                    content =
                        """
                        package com.example

                        class KotlinTestClass {
                            fun veryLongMethodNameThatExceedsTheMaximumLineLength(): String {
                                return "This line is too long and should trigger a Detekt violation"
                            }
                        }

                        """.trimIndent(),
                )

        val result = builder.runGradleAndFail("detekt")
        result.task(":detekt")?.outcome shouldBe TaskOutcome.FAILED

        val xmlReport = builder.projectDir.resolve("build/reports/detekt/detekt.xml")
        val htmlReport = builder.projectDir.resolve("build/reports/detekt/detekt.html")
        val sarifReport = builder.projectDir.resolve("build/reports/detekt/detekt.sarif")
        xmlReport.shouldExist()
        htmlReport.shouldExist()
        sarifReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("error")

        violations.length shouldBe 1
    }

    @Test
    @DisplayName("should execute Detekt using baseline")
    fun `execute detekt with baseline config`() {
        builder =
            TestProjectBuilder
                .create("detekt-baseline-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        kotlin("jvm")
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    ${basicPluginConfiguration()}

                    """.trimIndent(),
                ).withKotlinSource(
                    content =
                        """
                        package com.example

                        class KotlinTestClass {
                            fun veryLongMethodNameThatExceedsTheMaximumLineLength(): String {
                                return "This line is too long and should trigger a Detekt violation"
                            }
                        }

                        """.trimIndent(),
                )

        val detektBaselineResult = builder.runGradle("detektBaseline")
        detektBaselineResult.task(":detektBaseline")?.outcome shouldBe TaskOutcome.SUCCESS

        val detektResult = builder.runGradle("detekt")
        detektResult.task(":detekt")?.outcome shouldBe TaskOutcome.SUCCESS

        val xmlReport = builder.projectDir.resolve("build/reports/detekt/detekt.xml")
        xmlReport.shouldExist()

        val xmlBaselineResult = builder.projectDir.resolve("config/detekt/detekt-baseline.xml")
        xmlBaselineResult.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(xmlReport.toFile())
        val violations = document.getElementsByTagName("error")

        violations.length shouldBe 0
    }

    @Test
    @DisplayName("should execute spotless with defaults")
    fun `execute spotless with defaults`() {
        builder =
            TestProjectBuilder
                .create("spotless-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        kotlin("jvm")
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    ${basicPluginConfiguration()}

                    """.trimIndent(),
                ).withKotlinSource(
                    content =
                        """
                        package com.example

                        class KotlinTestClass{
                        fun getMessage( ):String="Hello from KotlinTestClass"
                        }
                        """.trimIndent(),
                ).withJavaSource(
                    content =
                        """
                        package com.example;

                        public class JavaTestClass {
                        public String getMessage( ){
                        return"Hello from JavaTestClass";
                        }
                        }
                        """.trimIndent(),
                )

        val kotlinResult = builder.runGradleAndFail("spotlessKotlinCheck")
        kotlinResult.task(":spotlessKotlinCheck")?.outcome shouldBe TaskOutcome.FAILED

        val javaResult = builder.runGradleAndFail("spotlessJavaCheck")
        javaResult.task(":spotlessJavaCheck")?.outcome shouldBe TaskOutcome.FAILED

        val applyResult = builder.runGradle("spotlessApply")
        applyResult.task(":spotlessApply")?.outcome shouldBe TaskOutcome.SUCCESS
    }

    // Dokka
    @Test
    @DisplayName("should execute dokka with defaults")
    fun `execute dokka with defaults`() {
        builder =
            TestProjectBuilder
                .create("dokka-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(
                    """
                    plugins {
                        kotlin("jvm")
                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
                    }

                    ${basicPluginConfiguration()}

                    """.trimIndent(),
                ).withKotlinSource(
                    content =
                        """
                        package com.example

                        /**
                         * A test class for demonstrating Dokka documentation generation.
                         *
                         * @author Test Developer
                         * @since 1.0.0
                         */
                        class KotlinTestClass {
                            /**
                             * Returns a greeting message.
                             *
                             * @return A greeting string
                             */
                            fun getMessage(): String = "Hello from KotlinTestClass"
                        }
                        """.trimIndent(),
                )

        val result = builder.runGradle("dokkaHtml")
        result.task(":dokkaHtml")?.outcome shouldBe TaskOutcome.SUCCESS

        val htmlReport = builder.projectDir.resolve("build/dokka/html/index.html")
        htmlReport.shouldExist()
    }

    // Kover
    // ESLint
    // Codenarc
}
