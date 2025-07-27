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

import io.kotest.matchers.file.shouldExist
import io.kotest.matchers.shouldBe
import org.gradle.testkit.runner.TaskOutcome
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import utils.TestProjectBuilder
import utils.basicBuildScript
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@DisplayName("Quality Tools Integration Tests")
class QualityToolsIntegrationTest {
    @Test
    @DisplayName("should execute the checkstyle with defaults")
    fun `execute checkstyle with defaults`() {
        val testProject =
            TestProjectBuilder
                .create("checkstyle-test")
                .withVersionCatalog()
                .withSettingsGradle()
                .withBuildGradle(basicBuildScript())
                .withJavaSource()

        val result = testProject.runGradleAndFail("checkstyleMain")
        result.task(":checkstyleMain")?.outcome shouldBe TaskOutcome.FAILED

        val checkstyleXmlReport = File(testProject.projectDir, "build/reports/checkstyle/main.xml")
        val checkstyleHtmlReport = File(testProject.projectDir, "build/reports/checkstyle/main.html")
        checkstyleXmlReport.shouldExist()
        checkstyleHtmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(checkstyleXmlReport)
        val violations = document.getElementsByTagName("error")

        violations.length shouldBe 1
    }

    @Test
    @DisplayName("should suppress the checkstyle violations")
    fun `checkstyle should skip violations due to suppressions`() {
        val testProject =
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

        val result = testProject.runGradle("checkstyleMain")
        result.task(":checkstyleMain")?.outcome shouldBe TaskOutcome.SUCCESS

        val checkstyleXmlReport = File(testProject.projectDir, "build/reports/checkstyle/main.xml")
        checkstyleXmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(checkstyleXmlReport)
        val violations = document.getElementsByTagName("error")

        violations.length shouldBe 0
    }

    @Test
    @DisplayName("should execute spotbugs with defaults")
    fun `execute spotbugs with defaults`() {
        val testProject =
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

        val result = testProject.runGradleAndFail("spotbugsMain")
        result.task(":spotbugsMain")?.outcome shouldBe TaskOutcome.FAILED

        val spotbugsXmlReport = File(testProject.projectDir, "build/reports/spotbugs/main.xml")
        val spotbugsHtmlReport = File(testProject.projectDir, "build/reports/spotbugs/main.html")
        val spotbugsSarifReport = File(testProject.projectDir, "build/reports/spotbugs/main.sarif")
        spotbugsXmlReport.shouldExist()
        spotbugsHtmlReport.shouldExist()
        spotbugsSarifReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(spotbugsXmlReport)
        val violations = document.getElementsByTagName("BugInstance")

        violations.length shouldBe 1
    }

    @Test
    @DisplayName("should execute the spotbugs with exclusion filters")
    fun `execute spotbugs with exclude filters`() {
        val testProject =
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

        val result = testProject.runGradle("spotbugsMain")
        result.task(":spotbugsMain")?.outcome shouldBe TaskOutcome.SUCCESS

        val spotbugsXmlReport = File(testProject.projectDir, "build/reports/spotbugs/main.xml")
        spotbugsXmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(spotbugsXmlReport)
        val violations = document.getElementsByTagName("BugInstance")

        violations.length shouldBe 0
    }

    @Test
    @DisplayName("should execute pmd with defaults")
    fun `execute pmd with defaults`() {
        val testProject =
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

        val result = testProject.runGradleAndFail("pmdMain")
        result.task(":pmdMain")?.outcome shouldBe TaskOutcome.FAILED

        val pmdXmlReport = File(testProject.projectDir, "build/reports/pmd/main.xml")
        val pmdHtmlReport = File(testProject.projectDir, "build/reports/pmd/main.html")
        pmdXmlReport.shouldExist()
        pmdHtmlReport.shouldExist()

        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pmdXmlReport)
        val violations = document.getElementsByTagName("violation")

        println(pmdXmlReport.readText())

        violations.length shouldBe 1
    }

//    @Test
//    @DisplayName("should execute pmd with cpd enabled")
//    fun `execute pmd with cpd enabled`() {
//        val testProject =
//            TestProjectBuilder
//                .create("cpd-test")
//                .withVersionCatalog()
//                .withSettingsGradle()
//                .withBuildGradle(
//                    """
//                    plugins {
//                        id("io.github.aaravmahajanofficial.jenkins-gradle-convention-plugin")
//                    }
//
//                    jenkinsConvention {
//                        artifactId = "test-plugin"
//
//                        quality {
//                            pmd {
//                                enableCPD = true
//                            }
//                        }
//                    }
//
//                    """.trimIndent()
//                )
//                .withJavaSource(
//                    className = "DuplicateClass1",
//                    content = """
//                    package com.example;
//
//                    public class DuplicateClass1 {
//                        public void method1() {
//                            System.out.println("This is a longer method with more content");
//                            System.out.println("Adding more lines to exceed minimum token count");
//                            System.out.println("Line 3");
//                            System.out.println("Line 4");
//                            System.out.println("Line 5");
//                            int x = 10;
//                            int y = 20;
//                            int result = x + y;
//                            System.out.println("Result: " + result);
//                        }
//                    }
//                    """.trimIndent()
//                ).withJavaSource(
//                    className = "DuplicateClass2",
//                    content = """
//                    package com.example;
//
//                    public class DuplicateClass2 {
//                        public void method1() {
//                            System.out.println("This is a longer method with more content");
//                            System.out.println("Adding more lines to exceed minimum token count");
//                            System.out.println("Line 3");
//                            System.out.println("Line 4");
//                            System.out.println("Line 5");
//                            int x = 10;
//                            int y = 20;
//                            int result = x + y;
//                            System.out.println("Result: " + result);
//                        }
//                    }
//                    """.trimIndent()
//                )
//
//        val result = testProject.runGradleAndFail("cpdCheck")
//        result.task(":cpdCheck")?.outcome shouldBe TaskOutcome.FAILED
//
//        val pmdXmlReport = File(testProject.projectDir, "build/reports/pmd/main.xml")
//        pmdXmlReport.shouldExist()
//
//        val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(pmdXmlReport)
//        val violations = document.getElementsByTagName("violation")
//
//        println(pmdXmlReport.readText())
//
//        violations.length shouldBe 1
//    }
}
